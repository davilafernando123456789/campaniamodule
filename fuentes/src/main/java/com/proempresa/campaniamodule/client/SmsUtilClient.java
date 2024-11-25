package com.proempresa.campaniamodule.client;

import com.proempresa.campaniamodule.model.request.SmsUtilEnvioRequest;
import com.proempresa.campaniamodule.model.response.SmsUtilEnvioResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "SmsUtilClient", url = "${proempresa.smsUtil.url}")
public interface SmsUtilClient {
	
	@PostMapping("/sms/envio")
	SmsUtilEnvioResponse enviar(SmsUtilEnvioRequest obj);

}
