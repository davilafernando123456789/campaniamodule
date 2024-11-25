package com.proempresa.campaniamodule.service;

import com.proempresa.campaniamodule.model.response.ProcessResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IExcelCargarService {
    ProcessResponse procesarArchivoExcel(MultipartFile file) throws Exception;
}
