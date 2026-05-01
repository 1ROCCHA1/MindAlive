package com.mindalive.backend.controlador;

import com.mindalive.backend.documento.Conversacion;
import com.mindalive.backend.modelo.RegistroEjercicio;
import com.mindalive.backend.repositorio.RegistroEjercicioRepositorio;
import com.mindalive.backend.servicio.ServicioAsistente;
import com.mindalive.backend.servicio.ServicioPerfil;
import com.mindalive.backend.documento.PerfilMayor;
import com.mindalive.backend.repositorio.ConversacionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/centro-control")
public class ControladorCentroControl {

    @Autowired
    private RegistroEjercicioRepositorio registroEjercicioRepositorio;

    @Autowired
    private ServicioPerfil servicioPerfil;

    @Autowired
    private ConversacionRepositorio conversacionRepositorio;

    @Autowired
    private ServicioAsistente servicioAsistente;

    @GetMapping("/{mayorId}")
    public ResponseEntity<?> obtenerDatosCentroControl(@PathVariable Long mayorId) {
        try {
            Map<String, Object> datos = new HashMap<>();

            // Datos de juegos
            List<String> juegos = Arrays.asList("SIMON", "MEMORY", "OPERACIONES", "RARO");
            Map<String, Object> datosJuegos = new HashMap<>();

            for (String juego : juegos) {
                List<RegistroEjercicio> registros = registroEjercicioRepositorio
                        .findByMayorIdAndTipoJuegoOrderByFechaDesc(mayorId, juego);

                Map<String, Object> datosJuego = new HashMap<>();
                int nivelActual = registros.isEmpty() ? 1 : registros.get(0).getNivel();
                datosJuego.put("nivelActual", nivelActual);

                List<Map<String, Object>> ultimas = new ArrayList<>();
                for (int i = 0; i < Math.min(5, registros.size()); i++) {
                    RegistroEjercicio r = registros.get(i);
                    Map<String, Object> partida = new HashMap<>();
                    partida.put("nivel", r.getNivel());
                    partida.put("aciertos", r.getAciertos());
                    partida.put("errores", r.getErrores());
                    partida.put("fecha", r.getFecha().toString());
                    partida.put("subio", r.isSubioPorNivel());
                    partida.put("bajo", r.isBajoPorNivel());
                    ultimas.add(partida);
                }
                datosJuego.put("ultimasPartidas", ultimas);

                List<Map<String, Object>> historial = new ArrayList<>();
                for (int i = 0; i < Math.min(20, registros.size()); i++) {
                    RegistroEjercicio r = registros.get(i);
                    Map<String, Object> punto = new HashMap<>();
                    punto.put("nivel", r.getNivel());
                    punto.put("fecha", r.getFecha().toString());
                    historial.add(punto);
                }
                Collections.reverse(historial);
                datosJuego.put("historialNiveles", historial);

                datosJuegos.put(juego, datosJuego);
            }
            datos.put("juegos", datosJuegos);

            // Historial de bienestar
            List<Conversacion> conversaciones = conversacionRepositorio.findByMayorIdOrderByInicioDesc(mayorId);
            List<Map<String, Object>> historialBienestar = new ArrayList<>();
            for (Conversacion conv : conversaciones) {
                if (conv.getEvaluacionBienestar() != null) {
                    Map<String, Object> punto = new HashMap<>();
                    punto.put("fecha", conv.getInicio().toString());
                    punto.put("animo", conv.getEvaluacionBienestar().getAnimo());
                    punto.put("sociabilidad", conv.getEvaluacionBienestar().getSociabilidad());
                    punto.put("cognitivo", conv.getEvaluacionBienestar().getCognitivo());
                    punto.put("observacion", conv.getEvaluacionBienestar().getObservacion());
                    historialBienestar.add(punto);
                }
                if (historialBienestar.size() >= 20) break;
            }
            Collections.reverse(historialBienestar);
            datos.put("historialBienestar", historialBienestar);

            // Actividades sugeridas de hoy
            LocalDateTime inicioDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            List<Map<String, Object>> actividadesHoy = new ArrayList<>();
            for (Conversacion conv : conversaciones) {
                if (conv.getInicio() != null && conv.getInicio().isAfter(inicioDia)) {
                    if (conv.getActividadesSugeridas() != null) {
                        for (Conversacion.ActividadSugerida act : conv.getActividadesSugeridas()) {
                            Map<String, Object> actividad = new HashMap<>();
                            actividad.put("conversacionId", conv.getId());
                            actividad.put("descripcion", act.getDescripcion());
                            actividad.put("confirmadaPorMayor", act.isConfirmadaPorMayor());
                            actividad.put("aprobadaPorCuidador", act.isAprobadaPorCuidador());
                            actividad.put("editadaPorCuidador", act.getEditadaPorCuidador());
                            actividadesHoy.add(actividad);
                        }
                    }
                }
            }
            datos.put("actividadesHoy", actividadesHoy);

            // Estado reciente del perfil
            try {
                PerfilMayor perfil = servicioPerfil.obtenerPerfilCompleto(mayorId);
                datos.put("estadoReciente", perfil.getCapaEstadoReciente());
            } catch (Exception e) {
                datos.put("estadoReciente", "");
            }

            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{mayorId}/resumen-semanal")
    public ResponseEntity<?> generarResumenSemanal(@PathVariable Long mayorId) {
        try {
            LocalDateTime haceSieteDias = LocalDateTime.now().minusDays(7);

            List<String> juegos = Arrays.asList("SIMON", "MEMORY", "OPERACIONES", "RARO");
            StringBuilder contexto = new StringBuilder();
            contexto.append("Genera un resumen semanal del estado cognitivo y emocional del mayor basándote en estos datos:\n\n");

            for (String juego : juegos) {
                List<RegistroEjercicio> registros = registroEjercicioRepositorio
                        .findByMayorIdAndTipoJuegoOrderByFechaDesc(mayorId, juego);

                List<RegistroEjercicio> registrosSemana = registros.stream()
                        .filter(r -> r.getFecha() != null && r.getFecha().isAfter(haceSieteDias))
                        .collect(Collectors.toList());

                if (!registrosSemana.isEmpty()) {
                    int nivelInicio = registrosSemana.get(registrosSemana.size() - 1).getNivel();
                    int nivelFin = registrosSemana.get(0).getNivel();
                    long victorias = registrosSemana.stream().filter(r -> r.getErrores() == 0).count();
                    contexto.append("Juego ").append(juego).append(": ")
                            .append(registrosSemana.size()).append(" partidas, ")
                            .append(victorias).append(" victorias, nivel ")
                            .append(nivelInicio).append(" → ").append(nivelFin).append("\n");
                } else {
                    contexto.append("Juego ").append(juego).append(": sin actividad esta semana\n");
                }
            }

            List<Conversacion> conversaciones = conversacionRepositorio.findByMayorIdOrderByInicioDesc(mayorId);
            List<Conversacion> convSemana = conversaciones.stream()
                    .filter(c -> c.getInicio() != null && c.getInicio().isAfter(haceSieteDias))
                    .collect(Collectors.toList());

            int totalMensajes = convSemana.stream().mapToInt(c -> c.getMensajes().size()).sum();
            contexto.append("\nConversaciones con Mindi: ").append(totalMensajes).append(" mensajes esta semana\n");

            List<Conversacion> conEvaluacion = convSemana.stream()
                    .filter(c -> c.getEvaluacionBienestar() != null)
                    .collect(Collectors.toList());

            if (!conEvaluacion.isEmpty()) {
                double mediaAnimo = conEvaluacion.stream()
                        .mapToInt(c -> c.getEvaluacionBienestar().getAnimo()).average().orElse(5);
                double mediaSocial = conEvaluacion.stream()
                        .mapToInt(c -> c.getEvaluacionBienestar().getSociabilidad()).average().orElse(5);
                double mediaCognitivo = conEvaluacion.stream()
                        .mapToInt(c -> c.getEvaluacionBienestar().getCognitivo()).average().orElse(5);
                contexto.append(String.format("Media bienestar: ánimo=%.1f, sociabilidad=%.1f, cognitivo=%.1f\n",
                        mediaAnimo, mediaSocial, mediaCognitivo));
            }

            try {
                PerfilMayor perfil = servicioPerfil.obtenerPerfilCompleto(mayorId);
                if (!perfil.getCapaEstadoReciente().isEmpty()) {
                    contexto.append("\nEstado reciente observado: ").append(perfil.getCapaEstadoReciente()).append("\n");
                }
            } catch (Exception ignored) {}

            contexto.append("\nEl resumen debe ser claro, directo y útil para el cuidador. ");
            contexto.append("Máximo 200 palabras. Destaca tendencias importantes, tanto positivas como preocupantes.");

            String resumen = servicioAsistente.enviarMensaje(mayorId, contexto.toString());

            return ResponseEntity.ok(Map.of("resumen", resumen));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}