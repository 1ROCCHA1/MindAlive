package com.mindalive.backend.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

// Guarda el historial completo de conversaciones con la IA
@Document(collection = "conversaciones")
public class Conversacion {

    @Id
    private String id;

    private Long mayorId;

    private LocalDateTime inicio = LocalDateTime.now();

    private List<Mensaje> mensajes = new ArrayList<>();

    // -1.0 muy triste, 0 neutro, 1.0 muy positivo
    private double puntuacionAnimo;

    private List<String> temas = new ArrayList<>();

    private boolean generoAlerta = false;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getMayorId() { return mayorId; }
    public void setMayorId(Long mayorId) { this.mayorId = mayorId; }

    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }

    public List<Mensaje> getMensajes() { return mensajes; }
    public void setMensajes(List<Mensaje> mensajes) { this.mensajes = mensajes; }

    public double getPuntuacionAnimo() { return puntuacionAnimo; }
    public void setPuntuacionAnimo(double puntuacionAnimo) { this.puntuacionAnimo = puntuacionAnimo; }

    public List<String> getTemas() { return temas; }
    public void setTemas(List<String> temas) { this.temas = temas; }

    public boolean isGeneroAlerta() { return generoAlerta; }
    public void setGeneroAlerta(boolean generoAlerta) { this.generoAlerta = generoAlerta; }

    // Clase interna que representa cada mensaje del chat
    public static class Mensaje {
        // "usuario" o "asistente"
        private String rol;
        private String contenido;
        private LocalDateTime momento;

        public String getRol() { return rol; }
        public void setRol(String rol) { this.rol = rol; }

        public String getContenido() { return contenido; }
        public void setContenido(String contenido) { this.contenido = contenido; }

        public LocalDateTime getMomento() { return momento; }
        public void setMomento(LocalDateTime momento) { this.momento = momento; }
    }
}