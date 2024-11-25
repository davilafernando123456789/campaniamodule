package com.proempresa.campaniamodule.model.repository;

import com.proempresa.campaniamodule.dto.CampaniasDto;
import com.proempresa.campaniamodule.model.entity.Campania;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Transactional
public interface ICampaniaRepository extends JpaRepository<Campania, Long> {

    @Query("SELECT c FROM Campania c WHERE c.campania = ?1 AND c.telefono = ?2 ")
    Optional<Campania> findByCampaniaAndTelefono(String campania, String numTelefono);

    @Query("SELECT c FROM Campania c WHERE c.exito = 1 AND c.campania = ?1 AND c.telefono = ?2")
    Stream<Campania> findCampaniaExito(String campania, String telefono);
    default Optional<Campania> findByIndExito(String campania, String telefono) {
        try (Stream<Campania> stream = findCampaniaExito(campania, telefono )) {
            return stream.findFirst();
        }
    }

    @Modifying
    @Query("UPDATE Campania c SET c.fecVencimiento = :fecVencimiento WHERE c.campania = :nombreCampania")
    int updateFecVencimientoByNombre(@Param("fecVencimiento") LocalDateTime fecVencimiento, @Param("nombreCampania") String nombreCampania);

    @Query("SELECT new com.proempresa.campaniamodule.dto.CampaniasDto(c.campania, c.fecVencimiento, COUNT(c)) " +
            "FROM Campania c GROUP BY c.campania, c.fecVencimiento")
    List<CampaniasDto> findCampanias();

    @Query("SELECT COUNT(c) FROM Campania c WHERE c.campania = ?1")
    Long findCampaniaTotal(String campania);

    @Query("SELECT COUNT(c) FROM Campania c WHERE c.campania = ?1 AND c.respuesta = 1 ")
    Long findCampaniaRespondidos(String campania);

    @Query("SELECT COUNT(c) FROM Campania c WHERE c.campania = ?1 AND c.exito = 1 ")
    Long findCampaniaFinalizados(String campania);

    @Query("SELECT COUNT(c) FROM Campania c WHERE c.campania = ?1 AND c.respuesta <> 1")
    Long findCampaniaNoRespondidos(String campania);

    @Query("SELECT COUNT(c) FROM Notificacion n " +
            "JOIN Campania c ON n.codCampania = c.id " +
            "WHERE c.campania = ?1 AND n.indExito = 1")
    Long findCampaniaNotificadosExitosos(String campania);

    @Query("SELECT COUNT(c) FROM NotificacionLog n " +
            "JOIN Campania c ON n.codCampania = c.id " +
            "WHERE c.campania = ?1")
    Long findCampaniaNotificadosError(String campania);

}
