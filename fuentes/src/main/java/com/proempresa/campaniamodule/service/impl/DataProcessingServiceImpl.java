package com.proempresa.campaniamodule.service.impl;

import com.proempresa.campaniamodule.client.IUrlenmascararWorkClient;
import com.proempresa.campaniamodule.config.ResPropertiesConfig;
import com.proempresa.campaniamodule.dto.CampaniaDto;
import com.proempresa.campaniamodule.model.entity.Campania;
import com.proempresa.campaniamodule.model.entity.Colaborador;
import com.proempresa.campaniamodule.model.entity.ExcelLog;
import com.proempresa.campaniamodule.model.repository.ICampaniaRepository;
import com.proempresa.campaniamodule.model.repository.IColaboradorRepository;
import com.proempresa.campaniamodule.model.repository.IExcelErrorRepository;
import com.proempresa.campaniamodule.model.request.UrlRequest;
import com.proempresa.campaniamodule.model.response.UrlResponse;
import com.proempresa.campaniamodule.util.LoggerUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proempresa.apiutil.exception.BadRequestException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Service
@AllArgsConstructor
public class DataProcessingServiceImpl {

    private final ICampaniaRepository campaniaRepo;
    private final IColaboradorRepository colaboradorRepo;
    private final ResPropertiesConfig resPropertiesConfig;
    private final IExcelErrorRepository exelErrorRepository;
    private final IUrlenmascararWorkClient urlenmascararWorkClient;

    private static final int ERROR_LIMIT = 10;

    @Transactional
    public void processCustomerBatch(List<CampaniaDto> batch) {
        int errorCount = 0;
        String lastErrorMessage;

        for (CampaniaDto campania : batch) {
            try {

                if(isClienteExistente(campania)){
                    //Cliente ya existente en base de datos
                    continue;
                } else {
                    Campania nuevaCampania = buildCampania(campania);
                    Campania campaniaGuardada = campaniaRepo.save(nuevaCampania);
                    saveColaboradores(campaniaGuardada, campania);
                }
            } catch (Exception e) {
                errorCount++;
                lastErrorMessage = e.getMessage();
                logError(campania, lastErrorMessage);

                if (errorCount >= ERROR_LIMIT) {
                    throw new BadRequestException(lastErrorMessage);
                }
            }
        }
    }

    private Campania buildCampania(CampaniaDto campania){
        String urlCompleta = crearUrl(resPropertiesConfig.getDominioUrlCompleta(), campania);
        String urlEncriptada = acortarUrl(urlCompleta);
        String urlDominio = resPropertiesConfig.getDominioUrlAcortada() + "/" + urlEncriptada;

        return Campania.builder()
                .campania(campania.getCampania())
                .codTelefono(campania.getCodTelefonico())
                .telefono(campania.getTelefono())
                .nomCliente(campania.getNombre())
                .monto(campania.getMonto())
                .tasa(campania.getTasa())
                .hash(urlEncriptada)
                .url(urlCompleta)
                .urlPeticion(urlDominio)
                .fecCreacion(new Date())
                .build();
    }

    private void saveColaboradores(Campania campaniaGuardada, CampaniaDto campania) {
        List<Colaborador> colaboradores = Arrays.asList(
                Colaborador.builder()
                        .codCampania(campaniaGuardada.getId())
                        .telefono(campania.getCelular1())
                        .email(campania.getCorreo1())
                        .build(),
                Colaborador.builder()
                        .codCampania(campaniaGuardada.getId())
                        .telefono(campania.getCelular2())
                        .email(campania.getCorreo2())
                        .build()
        );

        colaboradorRepo.saveAll(colaboradores);
    }

    private boolean isClienteExistente(CampaniaDto campania){
        Optional<Campania> result = campaniaRepo.findByCampaniaAndTelefono(campania.getCampania(), campania.getTelefono() );
        if(result.isPresent()){
            return true;
        } else {
            return false;
        }
    }

    private String acortarUrl(String urlCompleta){
        UrlRequest urlRequest = new UrlRequest();
        urlRequest.setUrl(urlCompleta);

        try {
            UrlResponse response = urlenmascararWorkClient.acortarUrl(urlRequest);
            return response.getUrl();
        } catch (Exception e) {
            LoggerUtil.printError("Error al acortar url: ", e);
            throw new BadRequestException("Error al acortar la URL en el servicio urlenmascarar");
        }
    }

    private String crearUrl(String dominio, CampaniaDto campania) {
        StringBuilder urlBuilder = new StringBuilder(dominio).append("?");
        Map<String, String> params = new HashMap<>();
        params.put("campania", campania.getCampania());
        params.put("telefono", campania.getTelefono());

        return params.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .map(entry -> entry.getKey() + "=" + encodeValue(entry.getValue()))
                .collect(Collectors.joining("&", urlBuilder.toString(), ""));
    }

    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
    private void logError(CampaniaDto campania, String lastErrorMessage) {
        ExcelLog logEntry = ExcelLog.builder()
                .campania(campania.getCampania())
                .nombre(campania.getNombre())
                .telefono(campania.getTelefono())
                .errorMessage(lastErrorMessage)
                .fecCreacion(LocalDateTime.now())
                .build();
        exelErrorRepository.save(logEntry);
    }
}