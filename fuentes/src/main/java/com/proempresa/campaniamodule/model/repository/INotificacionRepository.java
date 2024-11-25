package com.proempresa.campaniamodule.model.repository;

import com.proempresa.campaniamodule.model.entity.Notificacion;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Transactional
public interface INotificacionRepository extends JpaRepository<Notificacion, Long> {
    @Query("SELECT n FROM Notificacion n " +
            "WHERE n.id = ?1 AND n.codColaborador = ?2 AND n.indExito = 1")
    Stream<Notificacion> findCampania(Long codCampania, Long codColaborador);
    default Optional<Notificacion> findByIndExito(Long codCampania, Long codColaborador) {
        try (Stream<Notificacion> stream = findCampania(codCampania, codColaborador)) {
            return stream.findFirst();
        }
    }
    @Query("""
        SELECT n FROM Notificacion n  WHERE n.codCampania = ?1
    """)
    List<Notificacion> findByCodCampania(Long codCampania);
}
