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
    @GetMapping("/memory/{mayorId}")
    public ResponseEntity<?> generarMemory(@PathVariable Long mayorId) {
        try {
            Map<String, Object> partida = servicioJuegos.generarMemory(mayorId);
            return ResponseEntity.ok(partida);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/memory/validar")
    public ResponseEntity<?> validarMemory(@RequestBody Map<String, Object> datos) {
        try {
            Long mayorId = Long.valueOf(datos.get("mayorId").toString());
            int parejasAcertadas = Integer.parseInt(datos.get("parejasAcertadas").toString());
            int parejasTotal = Integer.parseInt(datos.get("parejasTotal").toString());
            double tiempoMedio = Double.parseDouble(datos.get("tiempoMedio").toString());
            int erroresIntermedios = Integer.parseInt(datos.get("erroresIntermedios").toString());
            boolean sinTiempo = Boolean.parseBoolean(datos.get("sinTiempo").toString());

            Map<String, Object> resultado = servicioJuegos.validarMemory(
                    mayorId, parejasAcertadas, parejasTotal, tiempoMedio, erroresIntermedios, sinTiempo);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/operaciones/{mayorId}")
    public ResponseEntity<?> generarOperacion(@PathVariable Long mayorId) {
        try {
            return ResponseEntity.ok(servicioJuegos.generarOperacion(mayorId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/operaciones/validar")
    public ResponseEntity<?> validarOperaciones(@RequestBody Map<String, Object> datos) {
        try {
            Long mayorId = Long.valueOf(datos.get("mayorId").toString());
            int aciertos = Integer.parseInt(datos.get("aciertos").toString());
            int errores = Integer.parseInt(datos.get("errores").toString());
            double tiempoMedio = Double.parseDouble(datos.get("tiempoMedio").toString());
            return ResponseEntity.ok(servicioJuegos.validarOperaciones(mayorId, aciertos, errores, tiempoMedio));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/raro/{mayorId}")
    public ResponseEntity<?> generarRaro(@PathVariable Long mayorId) {
        try {
            return ResponseEntity.ok(servicioJuegos.generarRaro(mayorId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/raro/validar")
    public ResponseEntity<?> validarRaro(@RequestBody Map<String, Object> datos) {
        try {
            Long mayorId = Long.valueOf(datos.get("mayorId").toString());
            int aciertos = Integer.parseInt(datos.get("aciertos").toString());
            int errores = Integer.parseInt(datos.get("errores").toString());
            double tiempoMedio = Double.parseDouble(datos.get("tiempoMedio").toString());
            return ResponseEntity.ok(servicioJuegos.validarRaro(mayorId, aciertos, errores, tiempoMedio));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}