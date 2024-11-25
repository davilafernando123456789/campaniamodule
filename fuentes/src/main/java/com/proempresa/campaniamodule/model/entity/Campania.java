package com.proempresa.campaniamodule.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "SWB_DATA_CAMPANIA", schema = "CACTUS_SAFI")
public class Campania {
    @Id
    @Column(name = "COD_CAMPANIA")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NOM_CAMPANIA")
    private String campania;

    @Column(name = "COD_CLIENTE")
    private String codCliente;

    @Column(name = "NOM_CLIENTE")
    private String nomCliente;

    @Column(name = "TIP_DOCUMENTO")
    private String tipoDocumento;

    @Column(name = "NUM_DOCUMENTO")
    private String documento;

    @Column(name = "DES_EMAIL")
    private String email;

    @Column(name = "DES_DIR")
    private String direccion;

    @Column(name = "COD_TELEFONO")
    private String codTelefono;

    @Column(name = "NUM_TELEFONO")
    private String telefono;

    @Column(name = "DES_MONTO")
    private String monto;

    @Column(name = "DES_TASA")
    private String tasa;

    @Column(name = "IND_RESPUESTA")
    private int respuesta;

    @Column(name = "DES_URL")
    private String url;

    @Column(name = "DES_URL_ACORTADA")
    private String urlPeticion;

    @Column(name = "DES_COD_URL")
    private String hash;

    @Column(name = "IND_EXITO")
    private int exito;

    @Column(name = "FEC_VENCIMIENTO")
    private LocalDateTime fecVencimiento;

    @Column(name = "FEC_CREACION")
    private Date fecCreacion;

}