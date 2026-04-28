package com.mindalive.backend.controlador;

import com.mindalive.backend.servicio.ServicioRegistro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/registro")
public class ControladorRegistro {

    @Autowired
    private ServicioRegistro servicioRegistro;

    @PostMapping("/nuevo")
    public ResponseEntity<?> registrar(@RequestBody Map<String, String> datos) {
        try {
            Map<String, Object> resultado = servicioRegistro.registrarCuidadorYMayor(
                    datos.get("nombreCuidador"),
                    datos.get("emailCuidador"),
                    datos.get("contrasenaCuidador"),
                    datos.get("nombreMayor")
            );
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}