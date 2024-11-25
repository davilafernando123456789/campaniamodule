package com.proempresa.campaniamodule.model.repository;

import com.proempresa.campaniamodule.model.entity.ExcelLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IExcelErrorRepository extends JpaRepository<ExcelLog, Long> {
}
