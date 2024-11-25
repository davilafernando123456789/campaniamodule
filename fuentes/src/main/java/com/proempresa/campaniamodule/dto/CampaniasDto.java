package com.proempresa.campaniamodule.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampaniasDto {
    private String campania;
    private LocalDateTime fecVencimiento;
    private long totalRegistros;
}