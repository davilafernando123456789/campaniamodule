package com.proempresa.campaniamodule.model.request;

import lombok.Data;

@Data
public class SmsUtilEnvioRequest {
	
	private String message; 
	private String[] phones; 

}