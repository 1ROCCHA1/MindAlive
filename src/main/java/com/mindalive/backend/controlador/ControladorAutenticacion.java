package com.mindalive.backend.controlador;

import com.mindalive.backend.modelo.Usuario;
import com.mindalive.backend.modelo.VinculoFamiliar;
import com.mindalive.backend.repositorio.VinculoFamiliarRepositorio;
import com.mindalive.backend.servicio.ServicioAutenticacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class ControladorAutenticacion {

    @Autowired
    private ServicioAutenticacion servicioAutenticacion;

    @Autowired
    private VinculoFamiliarRepositorio vinculoFamiliarRepositorio;

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody Map<String, String> datos) {
        try {
            Usuario usuario = servicioAutenticacion.registrar(
                    datos.get("nombre"),
                    datos.get("email"),
                    datos.get("contrasena"),
                    datos.get("rol")
            );

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("id", usuario.getId());
            respuesta.put("nombre", usuario.getNombre());
            respuesta.put("email", usuario.getEmail());
            respuesta.put("rol", usuario.getRol());

            return ResponseEntity.ok(respuesta);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> datos) {
        try {
            Usuario usuario = servicioAutenticacion.login(
                    datos.get("email"),
                    datos.get("contrasena")
            );

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("id", usuario.getId());
            respuesta.put("nombre", usuario.getNombre());
            respuesta.put("email", usuario.getEmail());
            respuesta.put("rol", usuario.getRol());

            // Si es cuidador añadimos el id del mayor asociado
            if ("CUIDADOR".equals(usuario.getRol())) {
                List<VinculoFamiliar> vinculos = vinculoFamiliarRepositorio.findByFamiliarId(usuario.getId());
                if (!vinculos.isEmpty()) {
                    Usuario mayor = vinculos.get(0).getMayor();
                    respuesta.put("mayorId", mayor.getId());
                    respuesta.put("nombreMayor", mayor.getNombre());
                }
            }

            return ResponseEntity.ok(respuesta);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}