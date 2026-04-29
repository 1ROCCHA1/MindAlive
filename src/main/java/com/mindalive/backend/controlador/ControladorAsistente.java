package com.mindalive.backend.controlador;

import com.mindalive.backend.servicio.ServicioAsistente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/asistente")
public class ControladorAsistente {

    @Autowired
    private ServicioAsistente servicioAsistente;

    @PostMapping("/mensaje")
    public ResponseEntity<?> enviarMensaje(@RequestBody Map<String, Object> datos) {
        try {
            Long mayorId = Long.valueOf(datos.get("mayorId").toString());
            String mensaje = datos.get("mensaje").toString();
            String respuesta = servicioAsistente.enviarMensaje(mayorId, mensaje);
            return ResponseEntity.ok(Map.of("respuesta", respuesta));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage(), "causa", e.getClass().getName()));
        }
    }
}
