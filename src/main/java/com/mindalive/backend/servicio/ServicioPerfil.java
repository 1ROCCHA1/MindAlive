package com.mindalive.backend.servicio;

import com.mindalive.backend.documento.PerfilMayor;
import com.mindalive.backend.repositorio.PerfilMayorRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class ServicioPerfil {

    @Autowired
    private PerfilMayorRepositorio perfilMayorRepositorio;

    public PerfilMayor obtenerOCrearPerfil(Long mayorId) {
        return perfilMayorRepositorio.findByMayorId(mayorId)
                .orElseGet(() -> {
                    PerfilMayor nuevo = new PerfilMayor();
                    nuevo.setMayorId(mayorId);
                    nuevo.setCapaPermanente(new PerfilMayor.CapaPermanente());
                    nuevo.setCapaRasgosEstables(new ArrayList<>());
                    nuevo.setCapaEstadoReciente("");
                    nuevo.setUltimaActualizacion(LocalDateTime.now());
                    return perfilMayorRepositorio.save(nuevo);
                });
    }

    public PerfilMayor actualizarCapaPermanente(Long mayorId, PerfilMayor.CapaPermanente capaNueva) {
        PerfilMayor perfil = obtenerOCrearPerfil(mayorId);
        perfil.setCapaPermanente(capaNueva);
        perfil.setUltimaActualizacion(LocalDateTime.now());
        return perfilMayorRepositorio.save(perfil);
    }

    public PerfilMayor obtenerPerfilCompleto(Long mayorId) {
        return obtenerOCrearPerfil(mayorId);
    }
}