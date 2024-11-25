package com.proempresa.campaniamodule.client;

import com.proempresa.campaniamodule.model.request.WhatsappUtilRequest;
import com.proempresa.campaniamodule.model.response.WhatsappUtilResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "WhatsappUtilClient", url = "${proempresa.whatsappUtil.url}")
public interface WhatsappUtilClient {
	
	@PostMapping("/v1/whatsapp/template/personalized/send")
	WhatsappUtilResponse sendMessage(WhatsappUtilRequest obj);
}
