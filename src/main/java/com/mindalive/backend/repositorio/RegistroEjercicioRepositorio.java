package com.mindalive.backend.repositorio;

import com.mindalive.backend.modelo.RegistroEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RegistroEjercicioRepositorio extends JpaRepository<RegistroEjercicio, Long> {
    List<RegistroEjercicio> findByMayorIdAndTipoJuegoOrderByFechaDesc(Long mayorId, String tipoJuego);
    List<RegistroEjercicio> findByMayorIdOrderByFechaDesc(Long mayorId);
}