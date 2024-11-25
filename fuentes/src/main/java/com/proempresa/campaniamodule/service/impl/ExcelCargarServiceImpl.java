package com.proempresa.campaniamodule.service.impl;

import com.proempresa.campaniamodule.config.ResPropertiesConfig;
import com.proempresa.campaniamodule.dto.CampaniaDto;
import com.proempresa.campaniamodule.model.response.ProcessResponse;
import com.proempresa.campaniamodule.service.IExcelCargarService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ExcelCargarServiceImpl implements IExcelCargarService {
    private static final Logger logger = LoggerFactory.getLogger(ExcelCargarServiceImpl.class);

    private static final int ERROR_LIMIT = 20;
    // Updated regex to handle more complex CSV parsing, including quoted fields with commas
    private static final String CSV_DELIMITER = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private final ResPropertiesConfig resPropertiesConfig;
    private final DataProcessingServiceImpl dataProcessingServiceImpl;
    private ExecutorService executorService;
    private final AtomicInteger totalFilasProcesadas = new AtomicInteger(0);
    private final AtomicInteger totalFilasErroneas = new AtomicInteger(0);
    private final AtomicReference<String> ultimoError = new AtomicReference<>("");
    private String campania;
    private volatile boolean detenerProceso = false;

    @Autowired
    public ExcelCargarServiceImpl(ResPropertiesConfig resPropertiesConfig,
                                  DataProcessingServiceImpl dataProcessingServiceImpl) {
        this.resPropertiesConfig = resPropertiesConfig;
        this.dataProcessingServiceImpl = dataProcessingServiceImpl;
        initializeExecutorService();
    }
    // Método para inicializar o reiniciar el executor service
    private void initializeExecutorService() {
        // Usar un executor service que no se cierra abruptamente
        this.executorService = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(), // core pool size
                Runtime.getRuntime().availableProcessors() * 2, // max pool size
                60L, // keep alive time
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), // queue
                new ThreadFactory() {
                    private final AtomicInteger threadCounter = new AtomicInteger(0);
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("ExcelProcessingThread-" + threadCounter.incrementAndGet());
                        thread.setDaemon(true);
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy() // Prevents task rejection
        );
    }

    @Override
    public ProcessResponse procesarArchivoExcel(MultipartFile file) {
        // Reiniciar el executor service antes de cada procesamiento
        if (executorService.isShutdown() || executorService.isTerminated()) {
            initializeExecutorService();
        }

        reset();
        campania = FilenameUtils.removeExtension(file.getOriginalFilename());

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            List<CompletableFuture<Void>> futures = new ArrayList<>();
            List<CampaniaDto> batch = new ArrayList<>(resPropertiesConfig.getSizeBatch());

            // Leer y validar cabecera
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("El archivo está vacío");
            }

            // Usar el nuevo método de parseo para la cabecera
            String[] headers = parseCSVLine(headerLine);
            validateHeaders(headers);

            String line;
            int lineNumber = 1; // Para tracking de líneas con error

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (detenerProceso) break;

                try {
                    if (line.trim().isEmpty()) {
                        continue; // Saltar líneas vacías
                    }

                    CampaniaDto campania = createDataCampania(line);
                    if (campania != null) {
                        batch.add(campania);

                        if (batch.size() >= resPropertiesConfig.getSizeBatch()) {
                            List<CampaniaDto> currentBatch = new ArrayList<>(batch);
                            CompletableFuture<Void> future = CompletableFuture.runAsync(
                                    () -> processBatch(currentBatch),
                                    executorService
                            );
                            futures.add(future);
                            batch.clear();
                        }
                    }
                } catch (Exception e) {
                    String errorMsg = String.format("Error en línea %d: %s", lineNumber, e.getMessage());
                    manejarError(errorMsg);
                    if (detenerProceso) break;
                }
            }

            procesarBatchRestante(batch, futures);
            esperarProcesamiento(futures);


            try {
                // Esperar a que se completen todas las tareas
                CompletableFuture<Void> allOf = CompletableFuture.allOf(
                        futures.toArray(new CompletableFuture[0])
                );

                // Timeout configurable
                allOf.get(5, TimeUnit.MINUTES);
            } catch (TimeoutException e) {
                logger.error("Timeout al procesar archivo", e);
                manejarError("Timeout al procesar el archivo");
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error al esperar procesamiento", e);
                manejarError("Error al procesar: " + e.getMessage());
            }

        } catch (Exception e) {
            logger.error("Error general al procesar archivo", e);
            manejarError("Error general al procesar el archivo: " + e.getMessage());
        } finally {
            // Apagar el executor service de manera controlada
            shutdownExecutorService();
        }

        return construirResponse();
    }
    // Método para apagar el executor service de manera segura
    private void shutdownExecutorService() {
        if (executorService != null) {
            try {
                executorService.shutdown(); // Disable new tasks
                // Wait a while for existing tasks to terminate
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow(); // Cancel currently executing tasks
                    // Wait a while for tasks to respond to being canceled
                    if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                        logger.error("Executor service did not terminate");
                    }
                }
            } catch (InterruptedException ie) {
                // Preserve interrupt status
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    // Método de procesamiento de batch
    private void processBatch(List<CampaniaDto> batch) {
        if (detenerProceso || batch.isEmpty()) return;

        try {
            // Procesar el batch
            dataProcessingServiceImpl.processCustomerBatch(batch);

            // Incrementar contador de filas procesadas de manera thread-safe
            totalFilasProcesadas.addAndGet(batch.size());
        } catch (Exception e) {
            // Manejar errores por cada registro del batch
            batch.forEach(campaniaDto ->
                    manejarError("Error al procesar registro: " + campaniaDto.getTelefono() + " - " + e.getMessage())
            );
        }
    }

    private String[] parseCSVLine(String line) {
        List<String> campos = new ArrayList<>();
        StringBuilder campoActual = new StringBuilder();
        boolean dentroDeComillas = false;
        boolean escapando = false;

        // Eliminar corchetes inicial y final si existen
        if (line.startsWith("[") && line.endsWith("]")) {
            line = line.substring(1, line.length() - 1);
        }

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (escapando) {
                // Si estamos escapando, agregar el carácter actual
                campoActual.append(c);
                escapando = false;
            } else if (c == '\\') {
                // Comenzar un escape
                escapando = true;
            } else if (c == '"') {
                // Manejar comillas dobles
                if (dentroDeComillas && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Comillas dobles dentro de un campo entrecomillado
                    campoActual.append('"');
                    i++; // Saltar la siguiente comilla
                } else {
                    // Alternar estado de comillas
                    dentroDeComillas = !dentroDeComillas;
                }
            } else if (c == ',' && !dentroDeComillas) {
                // Agregar campo cuando hay una coma fuera de comillas
                campos.add(limpiarCampo(campoActual.toString()));
                campoActual = new StringBuilder();
            } else {
                // Agregar carácter al campo actual
                campoActual.append(c);
            }
        }

        // Agregar el último campo
        if (campoActual.length() > 0) {
            campos.add(limpiarCampo(campoActual.toString()));
        }

        // Convertir a array, asegurando 10 campos
        String[] resultado = new String[10];
        for (int i = 0; i < 10; i++) {
            resultado[i] = i < campos.size() ? campos.get(i) : "";
        }

        // Log de depuración
        logger.debug("Campos parseados: {}", (Object) resultado);

        return resultado;
    }

    private String limpiarCampo(String campo) {
        if (campo == null) return "";

        // Eliminar comillas al inicio y al final
        campo = campo.trim();
        if (campo.startsWith("\"") && campo.endsWith("\"")) {
            campo = campo.substring(1, campo.length() - 1);
        }

        // Limpiar valores específicos
        campo = campo.replace("S/", "")
                .replace(",", "")
                .replace("%", "")
                .replace("(URL)", "")
                .trim();

        return campo;
    }

    private CampaniaDto createDataCampania(String line) {
        try {
            // Loguear la línea original para depuración
            logger.debug("Procesando línea: {}", line);

            // Usar el nuevo método de parseo
            String[] campos = parseCSVLine(line);

            // Validar y construir DTO
            return CampaniaDto.builder()
                    .campania(this.campania)
                    .codTelefonico(campos[0])
                    .telefono(campos[1])
                    .nombre(campos[2])
                    .monto(campos[3])
                    .tasa(campos[4])
                    .celular1(campos[6])
                    .celular2(campos[7])
                    .correo1(campos[8])
                    .correo2(campos[9])
                    .build();
        } catch (Exception e) {
            // Mejorar mensaje de error
            String errorDetallado = "Error al procesar línea: " + e.getMessage() +
                    " - Línea original: [" + line + "]" +
                    " - Campos parseados: " + Arrays.toString(parseCSVLine(line));

            logger.error(errorDetallado);
            throw new IllegalArgumentException(errorDetallado);
        }
    }

    // Método de limpieza de valores
    private String cleanValue(String value) {
        if (value == null) return "";

        // Trim whitespace
        value = value.trim();

        // Eliminar comillas si existen
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }

        // Eliminar formato de moneda si es necesario
        value = value.replace("S/", "").replace(",", "").trim();

        return value;
    }

    private void validateFields(String[] campos) {
        if (campos == null || campos.length < 10) {
            throw new IllegalArgumentException(
                    String.format("Número de campos insuficiente. Esperados: %d, Encontrados: %d",
                            10, campos == null ? 0 : campos.length));
        }
    }
    private void validateHeaders(String[] headers) {
        String[] expectedHeaders = {
                "CODIGO_TELEFONICO", "NUMERO_TELEFONICO", "{{1}} NOMBRE",
                "{{2}} MONTO", "{{3}} TASA", "{{4}} URL",
                "CELULAR_1", "CELULAR_2", "CORREO_1", "CORREO_2"
        };

        if (headers.length < expectedHeaders.length) {
            throw new IllegalArgumentException("El archivo no contiene todas las columnas requeridas");
        }

        // Optional: Add more specific header validation if needed
    }


    private void procesarBatchRestante(List<CampaniaDto> batch, List<CompletableFuture<Void>> futures) {
        if (!batch.isEmpty() && !detenerProceso) {
            futures.add(CompletableFuture.runAsync(() -> processBatch(batch), executorService));
        }
    }

    private void esperarProcesamiento(List<CompletableFuture<Void>> futures) throws Exception {
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            allOf.get(1, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            manejarError("Timeout al procesar el archivo");
            futures.forEach(f -> f.cancel(true));
        }
    }

    private ProcessResponse construirResponse() {
        return ProcessResponse.builder()
                .filasProcesadas(totalFilasProcesadas.get())
                .filasErroneas(totalFilasErroneas.get())
                .error(ultimoError.get())
                .procesoDetenido(detenerProceso)
                .build();
    }
    private synchronized void manejarError(String mensajeError) {
        ultimoError.set(mensajeError);
        int erroresActuales = totalFilasErroneas.incrementAndGet();
        if (erroresActuales >= ERROR_LIMIT) {
            detenerProceso = true;
        }
    }

    private void reset() {
        totalFilasProcesadas.set(0);
        totalFilasErroneas.set(0);
        detenerProceso = false;
        ultimoError.set("");
    }
}