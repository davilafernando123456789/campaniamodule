package com.proempresa.campaniamodule.model.repository;

import com.proempresa.campaniamodule.model.entity.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IColaboradorRepository extends JpaRepository<Colaborador, Long> {
    List<Colaborador> findByCodCampania(Long codCampania);
}
