package com.proempresa.campaniamodule.service.impl;

import com.proempresa.campaniamodule.dto.CampaniasDto;
import com.proempresa.campaniamodule.model.repository.ICampaniaRepository;
import com.proempresa.campaniamodule.service.ICampaniaService;
import com.proempresa.campaniamodule.util.LoggerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proempresa.apiutil.dto.ResponseDto;
import proempresa.apiutil.exception.BadRequestException;
import proempresa.apiutil.exception.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaniaServiceImpl implements ICampaniaService {

    private final ICampaniaRepository campaniaRepo;

    @Override
    public ResponseDto<String> updateFecVencimiento(String campania, LocalDateTime fecha) {
        if (campania == null) {
            throw new BadRequestException("Nombre de campaña faltante.");
        }
        if (fecha == null) {
            throw new BadRequestException("Fecha de vencimiento faltante.");
        }
        // Ejecutar la actualización y obtener el número de registros afectados
        int registrosActualizados = campaniaRepo.updateFecVencimientoByNombre(fecha, campania);

        // Verificar si se actualizó algún registro
        if (registrosActualizados == 0) {
            throw new NotFoundException("No se encontró ninguna campaña con el nombre proporcionado.");
        }

        LoggerUtil.printInfo("Recurso guardado", campania);
        return ResponseDto.<String>builder()
                .result("Actualización exitosa")
                .build();
    }

    @Override
    public List<CampaniasDto> listCampanias() {
        List<CampaniasDto> campanias = campaniaRepo.findCampanias();
        LoggerUtil.printInfo("Recurso listadis", campanias.size());
        return campanias;
    }
}
