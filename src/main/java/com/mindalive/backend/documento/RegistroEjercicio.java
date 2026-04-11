package com.mindalive.backend.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

// Guarda el resultado de una sesión de ejercicio cognitivo
@Document(collection = "registros_ejercicios")
public class RegistroEjercicio {

    @Id
    private String id;

    private Long mayorId;

    // SIMON, MEMORIA, ARITMETICA, ODD_ONE_OUT
    private String tipoEjercicio;

    private List<Respuesta> respuestas = new ArrayList<>();

    // Porcentaje de aciertos 0-100
    private double puntuacion;

    // Tiempo medio de respuesta en milisegundos
    private long tiempoMedioRespuesta;

    private LocalDateTime fechaSesion = LocalDateTime.now();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getMayorId() { return mayorId; }
    public void setMayorId(Long mayorId) { this.mayorId = mayorId; }

    public String getTipoEjercicio() { return tipoEjercicio; }
    public void setTipoEjercicio(String tipoEjercicio) { this.tipoEjercicio = tipoEjercicio; }

    public List<Respuesta> getRespuestas() { return respuestas; }
    public void setRespuestas(List<Respuesta> respuestas) { this.respuestas = respuestas; }

    public double getPuntuacion() { return puntuacion; }
    public void setPuntuacion(double puntuacion) { this.puntuacion = puntuacion; }

    public long getTiempoMedioRespuesta() { return tiempoMedioRespuesta; }
    public void setTiempoMedioRespuesta(long tiempoMedioRespuesta) { this.tiempoMedioRespuesta = tiempoMedioRespuesta; }

    public LocalDateTime getFechaSesion() { return fechaSesion; }
    public void setFechaSesion(LocalDateTime fechaSesion) { this.fechaSesion = fechaSesion; }

    // Cada respuesta individual dentro de la sesión
    public static class Respuesta {
        private String pregunta;
        private String respuestaUsuario;
        private boolean correcta;
        private long tiempoRespuestaMs;

        public String getPregunta() { return pregunta; }
        public void setPregunta(String pregunta) { this.pregunta = pregunta; }

        public String getRespuestaUsuario() { return respuestaUsuario; }
        public void setRespuestaUsuario(String respuestaUsuario) { this.respuestaUsuario = respuestaUsuario; }

        public boolean isCorrecta() { return correcta; }
        public void setCorrecta(boolean correcta) { this.correcta = correcta; }

        public long getTiempoRespuestaMs() { return tiempoRespuestaMs; }
        public void setTiempoRespuestaMs(long tiempoRespuestaMs) { this.tiempoRespuestaMs = tiempoRespuestaMs; }
    }
}