package com.mindalive.backend.repositorio;

import com.mindalive.backend.modelo.VinculoFamiliar;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VinculoFamiliarRepositorio extends JpaRepository<VinculoFamiliar, Long> {
    List<VinculoFamiliar> findByFamiliarId(Long familiarId);
    List<VinculoFamiliar> findByMayorId(Long mayorId);
}