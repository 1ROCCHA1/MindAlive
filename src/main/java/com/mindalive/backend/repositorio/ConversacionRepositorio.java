package com.mindalive.backend.repositorio;

import com.mindalive.backend.documento.Conversacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ConversacionRepositorio extends MongoRepository<Conversacion, String> {
    List<Conversacion> findByMayorIdOrderByInicioDesc(Long mayorId);
}
