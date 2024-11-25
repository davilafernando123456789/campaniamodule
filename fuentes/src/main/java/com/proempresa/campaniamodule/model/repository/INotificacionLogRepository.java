package com.proempresa.campaniamodule.model.repository;

import com.proempresa.campaniamodule.model.entity.NotificacionLog;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface INotificacionLogRepository extends JpaRepository<NotificacionLog, Long> {
}
