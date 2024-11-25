package com.proempresa.campaniamodule.controller;

import com.proempresa.campaniamodule.dto.CampaniasDto;
import com.proempresa.campaniamodule.service.ICampaniaService;
import com.proempresa.campaniamodule.util.LoggerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proempresa.apiutil.dto.ResponseDto;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class CampaniaController {

    private final ICampaniaService service;

    @PostMapping("/campania/vencimiento")
    public ResponseEntity<ResponseDto<String>>  getStatusCampania(
            @RequestParam(value = "campania") String campania,
            @RequestBody LocalDateTime fechaVencimiento
    )
    {
        LoggerUtil.printInfo("Entrando al controlador", campania);
        ResponseDto<String> response = service.updateFecVencimiento(campania, fechaVencimiento);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/campania/list")
    public ResponseEntity<List<CampaniasDto>> getStatusCampania()
    {
        LoggerUtil.printInfo("Entrando al controlador");
        List<CampaniasDto> response = service.listCampanias();
        return ResponseEntity.ok(response);
    }
}