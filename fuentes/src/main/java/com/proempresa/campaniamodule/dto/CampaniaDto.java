package com.proempresa.campaniamodule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CampaniaDto {
    private String campania;
    private String codTelefonico;
    private String telefono;
    private String nombre;
    private String monto;
    private String tasa;
    private String celular1;
    private String celular2;
    private String correo1;
    private String correo2;
}
