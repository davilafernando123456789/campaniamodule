package com.proempresa.campaniamodule.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class ResPropertiesConfig {

    @Value("${proempresa.schema}")
    private String schemaName;

    @Value("${proempresa.predominante}")
    private String envioPreferencial;

    @Value("${proempresa.whatsappUtil.template}")
    private String template;

    @Value("${proempresa.whatsappUtil.language}")
    private String lenguage;

    @Value("${proempresa.process.batch}")
    private int sizeBatch;

    @Value("${proempresa.dominio.url.completa}")
    private String dominioUrlCompleta;

    @Value("${proempresa.dominio.url.acortada}")
    private String dominioUrlAcortada;


}