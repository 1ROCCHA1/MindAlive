package com.mindalive.backend.repositorio;

import com.mindalive.backend.modelo.Alarma;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AlarmaRepositorio extends JpaRepository<Alarma, Long> {
    List<Alarma> findByMayorIdAndActivaTrue(Long mayorId);
    List<Alarma> findByMayorIdAndTipoAndActivaTrue(Long mayorId, String tipo);
    List<Alarma> findByMayorIdAndFechaHoraBetweenAndActivaTrue(Long mayorId, LocalDateTime inicio, LocalDateTime fin);
}