package com.proempresa.campaniamodule.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "SWB_DATA_CAMPANIA_LOG", schema = "CACTUS_SAFI")
public class ExcelLog {
    @Id
    @Column(name = "COD_ERROR")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NOM_CAMPANIA")
    private String campania;

    @Column(name = "COD_CLI")
    private String codCliente;

    @Column(name = "NOM_CLIENTE")
    private String nombre;

    @Column(name = "DIR_EMAIL_CLI")
    private String email;

    @Column(name = "NUM_TEL_CLI")
    private String telefono;

    @Column(name = "DES_ERROR")
    private String errorMessage;

    @Column(name = "FEC_CREACION")
    private LocalDateTime fecCreacion = LocalDateTime.now();

}