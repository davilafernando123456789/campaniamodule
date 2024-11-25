package com.proempresa.campaniamodule.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "SWB_CAMPANIA_NOTIFICAR_LOG", schema = "CACTUS_SAFI")
public class NotificacionLog {
    @Id
    @Column(name = "COD_NOTIFICACION")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codNotificacion;

    @Column(name = "COD_CAMPANIA", unique = true)
    private Long codCampania;

    @Column(name = "TIP_ENVIO")
    private String tipEnvio;

    @Column(name = "NUM_TELEFONO")
    private String numTelefono;

    @Column(name = "DIR_EMAIL")
    private String dirEmail;

    @Column(name = "DES_ERROR")
    private String error;

    @Column(name = "FEC_ENVIO")
    private LocalDateTime fecEnvio;

    @Column(name = "FEC_CREACION")
    private LocalDateTime fecCreacion;
}

