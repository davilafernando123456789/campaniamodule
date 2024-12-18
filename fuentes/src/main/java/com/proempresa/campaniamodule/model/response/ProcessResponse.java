package com.proempresa.campaniamodule.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessResponse {
    private int filasProcesadas;
    private int filasErroneas;
    private String error;
    private boolean procesoDetenido;
}