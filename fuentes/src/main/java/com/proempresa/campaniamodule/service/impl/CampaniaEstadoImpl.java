package com.proempresa.campaniamodule.service.impl;

import com.proempresa.campaniamodule.model.repository.ICampaniaRepository;
import com.proempresa.campaniamodule.model.response.EstadoResponse;
import com.proempresa.campaniamodule.service.ICampaniaEstadoService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import proempresa.apiutil.exception.BadRequestException;

@Service
@AllArgsConstructor
public class CampaniaEstadoImpl implements ICampaniaEstadoService {
    private final ICampaniaRepository campaniaRepo;

    @Override
    @Transactional
    public EstadoResponse getStatusCampania(String campania){
        EstadoResponse response = new EstadoResponse();
        if(campania == null || campania.isEmpty()){
            throw new BadRequestException("La campaña no es valida.");
        }
        Long totalMensajes = campaniaRepo.findCampaniaTotal(campania);
        if(totalMensajes == 0){
            throw new BadRequestException("No se encontró la campaña.");
        } else {

            Long totalRespondidos = campaniaRepo.findCampaniaRespondidos(campania);
            Long totalIgnorados = campaniaRepo.findCampaniaNoRespondidos(campania);
            Long totalFinalizados = campaniaRepo.findCampaniaFinalizados(campania);
            Long totalNotificacionesExitosas = campaniaRepo.findCampaniaNotificadosExitosos(campania);
            Long totalNotificadosErroneos = campaniaRepo.findCampaniaNotificadosError(campania);

            response.setTotal(String.valueOf(totalMensajes));
            response.setRespondidos(String.valueOf(totalRespondidos));
            response.setIgnorados(String.valueOf(totalIgnorados));
            response.setFinalizados(String.valueOf(totalFinalizados));
            response.setNotificacionesExitosas(String.valueOf(totalNotificacionesExitosas));
            response.setNotificacionesFallidas(String.valueOf(totalNotificadosErroneos));
            return response;
        }
    }
}
