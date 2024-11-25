package com.proempresa.campaniamodule.model.request;

import lombok.Data;

@Data
public class EmailUtilEnvioRequest {
	private String to;
	private String subject;
	private String body;
}