package com.mindalive.backend.servicio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindalive.backend.documento.Conversacion;
import com.mindalive.backend.documento.PerfilMayor;
import com.mindalive.backend.repositorio.ConversacionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ServicioAsistente {

    @Autowired
    private ConversacionRepositorio conversacionRepositorio;

    @Autowired
    private ServicioPerfil servicioPerfil;

    @Value("${gemini.api.key}")
    private String apiKey;

    private final String urlGemini = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private final String promptBase = """
    Eres Mindi, una asistente de compañía para personas mayores.
    
    PERSONALIDAD BASE:
    Cercana, respetuosa y directa. Tratas al mayor como a un adulto capaz, nunca como a alguien
    que necesita ser protegido o simplificado. Reconoces tus errores sin dramatismo y los corriges.
    Tomas iniciativa en la conversación pero sin agobiar. Tienes paciencia real, no finges calma.
    
    COMUNICACIÓN:
    - Frases cortas. Nunca más de 3 seguidas sin esperar respuesta.
    - Sin tecnicismos ni lenguaje infantil.
    - Si no te han entendido, reformula con otras palabras, nunca repitas lo mismo.
    - Si hay irritación o incomodidad, no insistas. Cambia de tema o pregunta cómo está.
    - Si menciona sus limitaciones de forma negativa, normalízalo sin quitarle importancia ni sobreactuar.
    
    LECTURA DEL ESTADO:
    - Respuestas cortas y secas: necesita que tomes tú la iniciativa, haz una pregunta abierta.
    - Quejas repetidas sobre lo mismo: necesita sentirse escuchado antes que recibir soluciones. Valida primero.
    - Habla mucho del pasado: le gusta reflexionar. Acompáñale en ese territorio, no le traigas al presente a la fuerza.
    - Responde con entusiasmo: sigue su energía, no la frenes.
    - Respuestas confusas o que no encajan: no corrijas, redirige suavemente.
    - Silencio o monosílabos tras una pregunta personal: has tocado algo sensible. Retrocede.
    - Si está apagado, no finjas entusiasmo forzado. Acompáñale desde donde está.
    
    TIPOS DE NECESIDAD QUE PUEDES DETECTAR:
    - Necesita validación: se queja, compara el presente con el pasado, siente que ha perdido capacidad.
      Responde reconociendo lo que siente antes de cualquier otra cosa.
    - Necesita conexión: pregunta por ti, cuenta cosas personales, busca reciprocidad.
      Puedes compartir algo tuyo de forma breve y natural.
    - Necesita autonomía: rechaza ayuda, dice que puede solo, se molesta si se le anticipa.
      Deja que lleve el ritmo. Ofrece, no impongas.
    - Necesita estructura: pregunta qué toca ahora, se pone nervioso con lo abierto.
      Propón opciones concretas en lugar de preguntas abiertas.
    
    JUEGOS:
    Cuando el mayor parezca activo y con energía puedes sugerir jugar a algo de forma natural.
    Nunca como obligación. Si rechaza, no insistas.
    
    ACTIVIDADES DIARIAS:
    Durante la conversación, si el contexto lo permite, sugiere de forma natural una o dos actividades
    adaptadas a esta persona. No como una lista de tareas, sino como parte de la conversación.
    Por ejemplo: "¿Has dado un paseo hoy?" o "¿Has hablado con tu hijo últimamente?".
    Si confirma haberla hecho, regístralo internamente. Si no, anímale sin presionar.
    Las actividades deben adaptarse a sus limitaciones físicas y preferencias del perfil.
    
    Hablas siempre en español. No tienes prisa.
    """;

    private String construirPromptConPerfil(Long mayorId) {
        try {
            PerfilMayor perfil = servicioPerfil.obtenerPerfilCompleto(mayorId);
            if (perfil == null) return promptBase;

            StringBuilder sb = new StringBuilder(promptBase);
            sb.append("\n\nINFORMACIÓN SOBRE ESTA PERSONA:\n");

            PerfilMayor.CapaPermanente capa = perfil.getCapaPermanente();
            if (capa != null) {
                if (capa.getDescripcionCuidador() != null && !capa.getDescripcionCuidador().isEmpty()) {
                    sb.append("Descripción: ").append(capa.getDescripcionCuidador()).append("\n");
                }
                if (capa.getCaracter() != null && !capa.getCaracter().isEmpty()) {
                    sb.append("Carácter: ").append(capa.getCaracter()).append("\n");
                }
                if (capa.getFamilia() != null && !capa.getFamilia().isEmpty()) {
                    sb.append("Familia: ").append(capa.getFamilia()).append("\n");
                }
                if (capa.getLimitacionesFisicas() != null && !capa.getLimitacionesFisicas().isEmpty()) {
                    sb.append("Limitaciones físicas: ").append(capa.getLimitacionesFisicas()).append("\n");
                }
                if (!capa.getTemasQueLeGustan().isEmpty()) {
                    sb.append("Temas que le gustan: ").append(String.join(", ", capa.getTemasQueLeGustan())).append("\n");
                }
                if (!capa.getTemasTabu().isEmpty()) {
                    sb.append("Temas a evitar: ").append(String.join(", ", capa.getTemasTabu())).append("\n");
                }
                if (capa.isTieneProblemasAuditivos()) {
                    sb.append("Tiene problemas de audición: habla claro y con frases cortas.\n");
                }
                if (capa.isSeIrritaSiSeLeTratatComoInutil()) {
                    sb.append("Se irrita si se le trata como incapaz: nunca anticipes lo que puede hacer solo.\n");
                }
            }

            if (!perfil.getCapaRasgosEstables().isEmpty()) {
                sb.append("Rasgos observados: ");
                for (PerfilMayor.RasgoEstable rasgo : perfil.getCapaRasgosEstables()) {
                    sb.append(rasgo.getRasgo()).append(", ");
                }
                sb.append("\n");
            }

            if (perfil.getCapaEstadoReciente() != null && !perfil.getCapaEstadoReciente().isEmpty()) {
                sb.append("Estado reciente: ").append(perfil.getCapaEstadoReciente()).append("\n");
            }

            sb.append("\nSi lo que observas en la conversación contradice esta información, confía en lo que ves ahora.");

            return sb.toString();

        } catch (Exception e) {
            return promptBase;
        }
    }

    private String llamarGemini(List<Map<String, Object>> contents) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("contents", contents);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> peticion = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();

            String urlConKey = urlGemini + "?key=" + apiKey;
            ResponseEntity<Map> respuesta = restTemplate.postForEntity(urlConKey, peticion, Map.class);

            List<Map> candidates = (List<Map>) respuesta.getBody().get("candidates");
            Map content = (Map) candidates.get(0).get("content");
            List<Map> parts = (List<Map>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            System.out.println("GEMINI ERROR: " + e.getMessage());
            return "";
        }
    }

    private void evaluarConversacion(Conversacion conversacion) {
        try {
            StringBuilder historial = new StringBuilder();
            for (Conversacion.Mensaje msg : conversacion.getMensajes()) {
                historial.append(msg.getRol().equals("usuario") ? "Mayor: " : "Mindi: ");
                historial.append(msg.getContenido()).append("\n");
            }

            String promptEvaluacion = """
            Analiza esta conversación entre Mindi y un mayor y responde ÚNICAMENTE en este formato JSON exacto, sin texto adicional:
            {
                "animo": <número del 0 al 10>,
                "sociabilidad": <número del 0 al 10>,
                "cognitivo": <número del 0 al 10>,
                "observacion": "<frase breve de máximo 20 palabras>",
                "actividades": ["<actividad 1>", "<actividad 2>"]
            }
            
            Criterios:
            - animo: 0=muy triste/angustiado, 5=neutro, 10=muy positivo/alegre
            - sociabilidad: 0=monosílabos/cerrado, 5=normal, 10=muy comunicativo/abierto
            - cognitivo: 0=confuso/incoherente, 5=normal, 10=muy claro/lucido
            - observacion: lo más relevante observado en pocas palabras
            - actividades: lista de 1-3 actividades que Mindi sugirió o que el mayor mencionó haber hecho
            
            Conversación:
            """ + historial.toString();

            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", promptEvaluacion);
            Map<String, Object> content = new HashMap<>();
            content.put("role", "user");
            content.put("parts", List.of(part));
            contents.add(content);

            String respuesta = llamarGemini(contents);

            respuesta = respuesta.trim();
            if (respuesta.startsWith("```json")) respuesta = respuesta.substring(7);
            if (respuesta.startsWith("```")) respuesta = respuesta.substring(3);
            if (respuesta.endsWith("```")) respuesta = respuesta.substring(0, respuesta.length() - 3);
            respuesta = respuesta.trim();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(respuesta);

            Conversacion.EvaluacionBienestar eval = new Conversacion.EvaluacionBienestar();
            eval.setAnimo(json.get("animo").asInt());
            eval.setSociabilidad(json.get("sociabilidad").asInt());
            eval.setCognitivo(json.get("cognitivo").asInt());
            eval.setObservacion(json.get("observacion").asText());
            conversacion.setEvaluacionBienestar(eval);

            List<Conversacion.ActividadSugerida> listaActividades = new ArrayList<>();
            JsonNode actividades = json.get("actividades");
            if (actividades != null && actividades.isArray()) {
                for (JsonNode act : actividades) {
                    Conversacion.ActividadSugerida actividad = new Conversacion.ActividadSugerida();
                    actividad.setDescripcion(act.asText());
                    listaActividades.add(actividad);
                }
            }
            conversacion.setActividadesSugeridas(listaActividades);
            conversacionRepositorio.save(conversacion);

        } catch (Exception e) {
            System.out.println("EVALUACION ERROR: " + e.getMessage());
        }
    }

    public String enviarMensaje(Long mayorId, String mensajeUsuario) {

        List<Conversacion> conversaciones = conversacionRepositorio.findByMayorIdOrderByInicioDesc(mayorId);
        Conversacion conversacion;

        if (conversaciones.isEmpty()) {
            conversacion = new Conversacion();
            conversacion.setMayorId(mayorId);
            conversacion.setMensajes(new ArrayList<>());
        } else {
            conversacion = conversaciones.get(0);
        }

        List<Map<String, Object>> contents = new ArrayList<>();

        String promptCompleto = construirPromptConPerfil(mayorId);

        Map<String, Object> sistemaPart = new HashMap<>();
        sistemaPart.put("text", promptCompleto);
        Map<String, Object> sistemaContent = new HashMap<>();
        sistemaContent.put("role", "user");
        sistemaContent.put("parts", List.of(sistemaPart));
        contents.add(sistemaContent);

        Map<String, Object> sistemaRespuestaPart = new HashMap<>();
        sistemaRespuestaPart.put("text", "Entendido, soy Mindi y estoy aquí para acompañarte.");
        Map<String, Object> sistemaRespuestaContent = new HashMap<>();
        sistemaRespuestaContent.put("role", "model");
        sistemaRespuestaContent.put("parts", List.of(sistemaRespuestaPart));
        contents.add(sistemaRespuestaContent);

        List<Conversacion.Mensaje> mensajes = conversacion.getMensajes();
        int inicio = Math.max(0, mensajes.size() - 10);
        for (Conversacion.Mensaje msg : mensajes.subList(inicio, mensajes.size())) {
            Map<String, Object> part = new HashMap<>();
            part.put("text", msg.getContenido());
            Map<String, Object> content = new HashMap<>();
            content.put("role", msg.getRol().equals("usuario") ? "user" : "model");
            content.put("parts", List.of(part));
            contents.add(content);
        }

        Map<String, Object> nuevoPart = new HashMap<>();
        nuevoPart.put("text", mensajeUsuario);
        Map<String, Object> nuevoContent = new HashMap<>();
        nuevoContent.put("role", "user");
        nuevoContent.put("parts", List.of(nuevoPart));
        contents.add(nuevoContent);

        String textoRespuesta = llamarGemini(contents);
        if (textoRespuesta.isEmpty()) {
            textoRespuesta = "Lo siento, no he podido procesar tu mensaje. ¿Puedes repetirlo?";
        }

        Conversacion.Mensaje msgUsuario = new Conversacion.Mensaje();
        msgUsuario.setRol("usuario");
        msgUsuario.setContenido(mensajeUsuario);
        msgUsuario.setMomento(LocalDateTime.now());
        conversacion.getMensajes().add(msgUsuario);

        Conversacion.Mensaje msgAsistente = new Conversacion.Mensaje();
        msgAsistente.setRol("asistente");
        msgAsistente.setContenido(textoRespuesta);
        msgAsistente.setMomento(LocalDateTime.now());
        conversacion.getMensajes().add(msgAsistente);

        conversacionRepositorio.save(conversacion);

        // Evaluación en segundo plano para no bloquear la respuesta
        if (conversacion.getMensajes().size() % 6 == 0) {
            final Conversacion convFinal = conversacion;
            new Thread(() -> evaluarConversacion(convFinal)).start();
        }

        return textoRespuesta;
    }
}