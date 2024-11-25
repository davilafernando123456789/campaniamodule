package com.proempresa.campaniamodule.service;

import com.proempresa.campaniamodule.model.response.CampaniaResponse;

public interface INotificacionService {
    CampaniaResponse sendNotificacionColaborador(String campania, String numTelefono);
}
