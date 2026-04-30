package com.mindalive.backend.controlador;

import com.mindalive.backend.documento.PerfilMayor;
import com.mindalive.backend.servicio.ServicioPerfil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/perfil")
public class ControladorPerfil {

    @Autowired
    private ServicioPerfil servicioPerfil;

    @GetMapping("/{mayorId}")
    public ResponseEntity<?> obtenerPerfil(@PathVariable Long mayorId) {
        try {
            PerfilMayor perfil = servicioPerfil.obtenerPerfilCompleto(mayorId);
            return ResponseEntity.ok(perfil);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{mayorId}/permanente")
    public ResponseEntity<?> actualizarCapaPermanente(@PathVariable Long mayorId,
                                                      @RequestBody PerfilMayor.CapaPermanente capa) {
        try {
            PerfilMayor perfil = servicioPerfil.actualizarCapaPermanente(mayorId, capa);
            return ResponseEntity.ok(perfil);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}