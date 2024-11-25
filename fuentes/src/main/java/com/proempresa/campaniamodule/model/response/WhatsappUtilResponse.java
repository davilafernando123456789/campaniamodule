package com.proempresa.campaniamodule.model.response;

import lombok.Data;
import java.util.List;

@Data
public class WhatsappUtilResponse {
	private String result;
	private Data data;

	@lombok.Data
	public static class Data {
		private List<Contact> contacts;
		private List<Message> messages;
		private String messaging_product;
	}

	@lombok.Data
	public static class Contact {
		private String input;
		private String wa_id;
	}

	@lombok.Data
	public static class Message {
		private String id;
		private String message_status;
	}
}