package com.proempresa.campaniamodule.service;


import com.proempresa.campaniamodule.dto.CampaniasDto;
import proempresa.apiutil.dto.ResponseDto;
import java.time.LocalDateTime;
import java.util.List;

public interface ICampaniaService {

    ResponseDto<String> updateFecVencimiento(String campania, LocalDateTime fecha);
    List<CampaniasDto> listCampanias();

}
