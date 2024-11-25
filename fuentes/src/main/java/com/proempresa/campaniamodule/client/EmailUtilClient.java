package com.proempresa.campaniamodule.client;

import com.proempresa.campaniamodule.model.request.EmailUtilEnvioRequest;
import com.proempresa.campaniamodule.model.response.EmailUtilEnvioResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "EmailUtilClient", url = "${proempresa.emailUtil.url}")
public interface EmailUtilClient {
	
	@PostMapping("/email/envio")
	EmailUtilEnvioResponse enviar(EmailUtilEnvioRequest obj);

}
