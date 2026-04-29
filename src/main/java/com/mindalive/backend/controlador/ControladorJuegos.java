package com.mindalive.backend.controlador;

import com.mindalive.backend.servicio.ServicioJuegos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/juegos")
public class ControladorJuegos {

    @Autowired
    private ServicioJuegos servicioJuegos;

    @GetMapping("/simon/{mayorId}")
    public ResponseEntity<?> generarSimon(@PathVariable Long mayorId) {
        try {
            Map<String, Object> partida = servicioJuegos.generarSimon(mayorId);
            return ResponseEntity.ok(partida);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/simon/validar")
    public ResponseEntity<?> validarSimon(@RequestBody Map<String, Object> datos) {
        try {
            Long mayorId = Long.valueOf(datos.get("mayorId").toString());
            List<String> secuenciaCorrecta = (List<String>) datos.get("secuenciaCorrecta");
            List<String> respuestaUsuario = (List<String>) datos.get("respuestaUsuario");
            double tiempoMedio = Double.parseDouble(datos.get("tiempoMedio").toString());

            Map<String, Object> resultado = servicioJuegos.validarSimon(
                    mayorId, secuenciaCorrecta, respuestaUsuario, tiempoMedio);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}