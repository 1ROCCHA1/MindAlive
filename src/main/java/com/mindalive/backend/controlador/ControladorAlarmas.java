package com.mindalive.backend.controlador;

import com.mindalive.backend.modelo.Alarma;
import com.mindalive.backend.servicio.ServicioAlarmas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alarmas")
public class ControladorAlarmas {

    @Autowired
    private ServicioAlarmas servicioAlarmas;

    // Crear alarma
    @PostMapping
    public ResponseEntity<?> crearAlarma(@RequestBody Alarma alarma) {
        try {
            Alarma nueva = servicioAlarmas.crearAlarma(alarma);
            return ResponseEntity.ok(nueva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Obtener todas las alarmas activas de un mayor
    @GetMapping("/mayor/{mayorId}")
    public ResponseEntity<?> obtenerAlarmas(@PathVariable Long mayorId) {
        try {
            List<Alarma> alarmas = servicioAlarmas.obtenerAlarmasActivas(mayorId);
            return ResponseEntity.ok(alarmas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Obtener alarmas por tipo
    @GetMapping("/mayor/{mayorId}/tipo/{tipo}")
    public ResponseEntity<?> obtenerAlarmasPorTipo(@PathVariable Long mayorId, @PathVariable String tipo) {
        try {
            List<Alarma> alarmas = servicioAlarmas.obtenerAlarmasPorTipo(mayorId, tipo);
            return ResponseEntity.ok(alarmas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Obtener alarmas de hoy
    @GetMapping("/mayor/{mayorId}/hoy")
    public ResponseEntity<?> obtenerAlarmasHoy(@PathVariable Long mayorId) {
        try {
            List<Alarma> alarmas = servicioAlarmas.obtenerAlarmasHoy(mayorId);
            return ResponseEntity.ok(alarmas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Actualizar alarma
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarAlarma(@PathVariable Long id, @RequestBody Alarma alarma) {
        try {
            return servicioAlarmas.actualizarAlarma(id, alarma)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Confirmar alarma
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarAlarma(@PathVariable Long id) {
        try {
            return servicioAlarmas.confirmarAlarma(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Desactivar alarma
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivarAlarma(@PathVariable Long id) {
        try {
            return servicioAlarmas.desactivarAlarma(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Eliminar alarma
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAlarma(@PathVariable Long id) {
        try {
            if (servicioAlarmas.eliminarAlarma(id)) {
                return ResponseEntity.ok(Map.of("mensaje", "Alarma eliminada"));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}