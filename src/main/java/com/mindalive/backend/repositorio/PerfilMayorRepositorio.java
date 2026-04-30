package com.mindalive.backend.repositorio;

import com.mindalive.backend.documento.PerfilMayor;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface PerfilMayorRepositorio extends MongoRepository<PerfilMayor, String> {
    Optional<PerfilMayor> findByMayorId(Long mayorId);
}