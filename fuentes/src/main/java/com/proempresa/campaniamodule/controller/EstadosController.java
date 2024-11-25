package com.proempresa.campaniamodule.controller;

import com.proempresa.campaniamodule.model.response.EstadoResponse;
import com.proempresa.campaniamodule.service.ICampaniaEstadoService;
import com.proempresa.campaniamodule.util.LoggerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class EstadosController {
    private final ICampaniaEstadoService service;

    @GetMapping("/campania/estado")
    public ResponseEntity<EstadoResponse> getStatusCampania(
            @RequestParam(value = "campania") String campania
    )
    {
        LoggerUtil.printInfo("Entrando al controlador", campania);
        EstadoResponse response = service.getStatusCampania(campania);
        return ResponseEntity.ok(response);
    }
}
