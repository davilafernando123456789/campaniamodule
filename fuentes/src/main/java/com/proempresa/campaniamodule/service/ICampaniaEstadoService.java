package com.proempresa.campaniamodule.service;

import com.proempresa.campaniamodule.model.response.EstadoResponse;

public interface ICampaniaEstadoService {
    EstadoResponse getStatusCampania(String campania);
}
