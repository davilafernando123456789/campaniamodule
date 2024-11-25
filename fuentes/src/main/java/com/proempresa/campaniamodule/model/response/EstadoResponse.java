package com.proempresa.campaniamodule.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoResponse {
    private String total;
    private String respondidos;
    private String ignorados;
    private String finalizados;
    private String notificacionesExitosas;
    private String notificacionesFallidas;
}
