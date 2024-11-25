package com.proempresa.campaniamodule.model.request;

import lombok.Data;
import java.util.List;

@Data
public class WhatsappUtilRequest {
	private String template;
	private String language;
	private List<String> variables;
	private String phone;
}