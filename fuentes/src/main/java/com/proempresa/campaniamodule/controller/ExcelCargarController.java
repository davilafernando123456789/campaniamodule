package com.proempresa.campaniamodule.controller;

import com.proempresa.campaniamodule.model.response.ProcessResponse;
import com.proempresa.campaniamodule.service.IExcelCargarService;
import com.proempresa.campaniamodule.util.LoggerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;
import proempresa.apiutil.exception.BadRequestException;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ExcelCargarController {
    private final IExcelCargarService iCargarService;

    @PostMapping(value="/cargar-excel")
    public ResponseEntity<?> cargarArchivoExcel(@RequestParam(name = "file") MultipartFile file) throws Exception{

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Suba un archivo csv válido.");
        }
        LoggerUtil.printInfo("cargar-excel");
        try {
            ProcessResponse response = iCargarService.procesarArchivoExcel(file);
            if (response.isProcesoDetenido()) {
                LoggerUtil.printInfo(String.valueOf(response));
                return ResponseEntity.badRequest().body(response);
            } else {
                return ResponseEntity.ok(response);
            }
        } catch (HttpStatusCodeException e) {
            LoggerUtil.printError("cargar-excel", e );
            return new ResponseEntity(e.getResponseBodyAsString(),e.getResponseHeaders(), e.getStatusCode());
        }
    }
}