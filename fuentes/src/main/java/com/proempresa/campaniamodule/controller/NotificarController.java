package com.proempresa.campaniamodule.controller;

import com.proempresa.campaniamodule.model.response.CampaniaResponse;
import com.proempresa.campaniamodule.service.INotificacionService;
import com.proempresa.campaniamodule.util.LoggerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/campania")
@RequiredArgsConstructor
public class NotificarController {
    private final INotificacionService service;
    @GetMapping("/colaborador/notificar")
    public ResponseEntity<CampaniaResponse> sendNotificationColaborador(
            @RequestParam(value = "campania") String campania,
            @RequestParam(value = "telefono") String telefono
    )
    {
        LoggerUtil.printInfo("Entrando al controlador", campania, telefono);
        CampaniaResponse response = service.sendNotificacionColaborador(campania, telefono);
        return ResponseEntity.ok(response);
    }
}
