package com.proempresa.campaniamodule.client;

import com.proempresa.campaniamodule.model.request.UrlRequest;
import com.proempresa.campaniamodule.model.response.UrlResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "IUrlenmascararWorkClient", url = "${proempresa.urlenmascarar.work.url}")
public interface IUrlenmascararWorkClient {
	
	@PostMapping("/v1/campania/url/acortar")
	UrlResponse acortarUrl(@RequestBody UrlRequest urlRequest);

}
