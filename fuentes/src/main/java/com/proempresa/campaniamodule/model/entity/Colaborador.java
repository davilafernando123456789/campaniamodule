package com.proempresa.campaniamodule.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "SWB_CAMPANIA_COLABORADOR", schema = "CACTUS_SAFI")
public class Colaborador {
    @Id
    @Column(name = "COD_COLABORADOR")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "COD_EJECUTIVO")
    private String ejecutivo;

    @Column(name = "COD_CAMPANIA")
    private Long codCampania;

    @Column(name = "NOM_COLABORADOR")
    private String nomColaborador;

    @Column(name = "TIP_DOCUMENTO")
    private String tipoDocumento;

    @Column(name = "NUM_DOCUMENTO")
    private String documento;

    @Column(name = "DES_EMAIL")
    private String email;

    @Column(name = "COD_TELEFONO")
    private String codTelefono;

    @Column(name = "NUM_TELEFONO")
    private String telefono;

    @Column(name = "FEC_CREACION")
    private LocalDateTime fecCreacion;
}
