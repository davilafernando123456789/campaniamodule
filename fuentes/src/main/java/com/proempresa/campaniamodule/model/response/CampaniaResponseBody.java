package com.proempresa.campaniamodule.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampaniaResponseBody {
    private String cliente;
    private String telefono;
    private String ejecutivo;
    private String telefonoEjecutivo;
    private String metodoEnvio;
}
