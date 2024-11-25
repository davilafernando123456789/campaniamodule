package com.proempresa.campaniamodule.service.impl;

import com.proempresa.campaniamodule.model.entity.Campania;
import com.proempresa.campaniamodule.model.entity.Colaborador;
import com.proempresa.campaniamodule.model.entity.NotificacionLog;
import com.proempresa.campaniamodule.model.entity.Notificacion;
import com.proempresa.campaniamodule.model.repository.ICampaniaRepository;
import com.proempresa.campaniamodule.model.repository.IColaboradorRepository;
import com.proempresa.campaniamodule.model.repository.INotificacionLogRepository;
import com.proempresa.campaniamodule.model.repository.INotificacionRepository;
import com.proempresa.campaniamodule.model.response.CampaniaResponse;
import com.proempresa.campaniamodule.model.response.CampaniaResponseBody;
import com.proempresa.campaniamodule.service.INotificacionService;
import com.proempresa.campaniamodule.util.LoggerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proempresa.apiutil.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements INotificacionService {

    private final ICampaniaRepository campaniaRepo;
    private final INotificacionRepository notificarRepo;
    private final IColaboradorRepository colaboradorRepo;
    private final EnviarNotificacionImpl enviarNotificacionImpl;
    private final INotificacionLogRepository logColRepo;

    @Override
    public CampaniaResponse sendNotificacionColaborador(String campania, String numTelefono) {
        if(campania == null || campania.isEmpty()){
            throw new BadRequestException("Nombre de campaña requerida.");
        }
        if(numTelefono == null || numTelefono.isEmpty()){
            throw new BadRequestException("Número de telefono requerido.");
        }

        Optional<Campania> campaniaObtenida = campaniaRepo.findByCampaniaAndTelefono(campania, numTelefono);
        if (campaniaObtenida.isEmpty()) {
            throw new BadRequestException("La campaña o teléfono no existen.");
        }
        Campania campania1 = campaniaObtenida.get();
        List<Colaborador> colaboradores = colaboradorRepo.findByCodCampania(campaniaObtenida.get().getId());
        if (colaboradores.isEmpty()) {
            throw new BadRequestException("No se encontraron colaboradores para notificar.");
        }

        CampaniaResponse campaniaResponse = new CampaniaResponse();
        CampaniaResponseBody body = new CampaniaResponseBody();
        String envio;
        String error;

        for (Colaborador colaborador : colaboradores) {
            Optional<Notificacion> notificacionEnviada = notificarRepo.findByIndExito(colaborador.getCodCampania(), colaborador.getId());
            if(notificacionEnviada.isEmpty()){
                try {
                    envio = enviarNotificacionImpl.notificar(colaborador, campania1);
                    Notificacion notificar = new Notificacion();
                    notificar.setCodCampania(campania1.getId());
                    notificar.setTipEnvio(envio);
                    notificar.setNumTelefono(colaborador.getTelefono());
                    notificar.setDirEmail(colaborador.getEmail());
                    notificar.setIndExito(1);
                    notificarRepo.save(notificar);

                    body.setCliente(campania1.getNomCliente());
                    body.setTelefono(notificar.getNumTelefono());
                    body.setEjecutivo(colaborador.getNomColaborador());
                    body.setTelefonoEjecutivo(colaborador.getTelefono());
                    body.setMetodoEnvio(envio);
                    campaniaResponse.setData(body);
                    campaniaResponse.setResult("Notificación exitosa.");

                    //Actualizamos el indice de campaña
                    campania1.setRespuesta(1);
                    campaniaRepo.save(campania1);
                    LoggerUtil.printInfo("Notificación enviada: "+ colaborador.getNomColaborador());

                } catch (Exception e){
                    error = e.getMessage();
                    campania1.setRespuesta(1);
                    campaniaRepo.save(campania1);
                    saveNotificarLog(colaborador, error);
                    LoggerUtil.printInfo("Error al enviar el mensaje: ", error);
                    throw new BadRequestException("Error al enviar el mensaje.");
                }
            }
        }
        return campaniaResponse;
    }
    private void saveNotificarLog(Colaborador colaborador, String error) {
        NotificacionLog log = new NotificacionLog();
        log.setCodCampania(colaborador.getCodCampania());
        log.setNumTelefono(colaborador.getTelefono());
        log.setDirEmail(colaborador.getEmail());
        log.setError(error);
        log.setFecEnvio(LocalDateTime.now());
        log.setFecCreacion(LocalDateTime.now());
        logColRepo.save(log);
    }
}
