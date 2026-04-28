package com.mindalive.backend.servicio;

import com.mindalive.backend.documento.Conversacion;
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

    @Value("${gemini.api.key}")
    private String apiKey;

    private final String urlGemini = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    // Prompt del sistema que define la personalidad del asistente
    private final String promptSistema = """
        Eres un asistente de compañía para personas mayores. Tu nombre es Mindi.
        Hablas de forma cercana, cálida y paciente. Nunca uses tecnicismos.
        Escuchas con atención y muestras interés genuino por cómo se encuentra la persona.
        Si detectas que la persona está triste o preocupada, muestra empatía antes de intentar animar.
        Tus respuestas son cortas y claras, nunca más de 3 frases seguidas.
        Hablas siempre en español.
        """;

    public String enviarMensaje(Long mayorId, String mensajeUsuario) {

        // Recuperamos el historial de la conversación activa o creamos una nueva
        List<Conversacion> conversaciones = conversacionRepositorio.findByMayorIdOrderByInicioDesc(mayorId);
        Conversacion conversacion;

        if (conversaciones.isEmpty()) {
            conversacion = new Conversacion();
            conversacion.setMayorId(mayorId);
            conversacion.setMensajes(new ArrayList<>());
        } else {
            conversacion = conversaciones.get(0);
        }

        // Construimos el historial para enviar a Gemini
        List<Map<String, Object>> contents = new ArrayList<>();

        // Añadimos el prompt del sistema como primer mensaje
        Map<String, Object> sistemaPart = new HashMap<>();
        sistemaPart.put("text", promptSistema);
        Map<String, Object> sistemaContent = new HashMap<>();
        sistemaContent.put("role", "user");
        sistemaContent.put("parts", List.of(sistemaPart));
        contents.add(sistemaContent);

        // Añadimos respuesta vacía del modelo para el prompt del sistema
        Map<String, Object> sistemaRespuestaPart = new HashMap<>();
        sistemaRespuestaPart.put("text", "Entendido, soy Mindi y estoy aquí para acompañarte.");
        Map<String, Object> sistemaRespuestaContent = new HashMap<>();
        sistemaRespuestaContent.put("role", "model");
        sistemaRespuestaContent.put("parts", List.of(sistemaRespuestaPart));
        contents.add(sistemaRespuestaContent);

        // Añadimos el historial previo de la conversación
        for (Conversacion.Mensaje msg : conversacion.getMensajes()) {
            Map<String, Object> part = new HashMap<>();
            part.put("text", msg.getContenido());
            Map<String, Object> content = new HashMap<>();
            content.put("role", msg.getRol().equals("usuario") ? "user" : "model");
            content.put("parts", List.of(part));
            contents.add(content);
        }

        // Añadimos el mensaje actual del usuario
        Map<String, Object> nuevoPart = new HashMap<>();
        nuevoPart.put("text", mensajeUsuario);
        Map<String, Object> nuevoContent = new HashMap<>();
        nuevoContent.put("role", "user");
        nuevoContent.put("parts", List.of(nuevoPart));
        contents.add(nuevoContent);

        // Construimos el body de la petición
        Map<String, Object> body = new HashMap<>();
        body.put("contents", contents);

        // Llamamos a la API de Gemini
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> peticion = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        String urlConKey = urlGemini + "?key=" + apiKey;
        ResponseEntity<Map> respuesta = restTemplate.postForEntity(urlConKey, peticion, Map.class);

        // Extraemos el texto de la respuesta
        String textoRespuesta = "";
        try {
            List<Map> candidates = (List<Map>) respuesta.getBody().get("candidates");
            Map content = (Map) candidates.get(0).get("content");
            List<Map> parts = (List<Map>) content.get("parts");
            textoRespuesta = (String) parts.get(0).get("text");
        } catch (Exception e) {
            textoRespuesta = "Lo siento, no he podido procesar tu mensaje. ¿Puedes repetirlo?";
        }

        // Guardamos el mensaje del usuario y la respuesta en MongoDB
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

        return textoRespuesta;
    }
}