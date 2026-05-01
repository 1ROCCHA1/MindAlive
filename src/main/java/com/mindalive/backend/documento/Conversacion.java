package com.mindalive.backend.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Document(collection = "conversaciones")
public class Conversacion {

    @Id
    private String id;

    private Long mayorId;

    private LocalDateTime inicio = LocalDateTime.now();

    private List<Mensaje> mensajes = new ArrayList<>();

    private double puntuacionAnimo;

    private List<String> temas = new ArrayList<>();

    private boolean generoAlerta = false;

    // Evaluación de bienestar al final de la conversación
    private EvaluacionBienestar evaluacionBienestar;

    // Actividades sugeridas por Mindi durante la conversación
    private List<ActividadSugerida> actividadesSugeridas = new ArrayList<>();

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

    public EvaluacionBienestar getEvaluacionBienestar() { return evaluacionBienestar; }
    public void setEvaluacionBienestar(EvaluacionBienestar e) { this.evaluacionBienestar = e; }

    public List<ActividadSugerida> getActividadesSugeridas() { return actividadesSugeridas; }
    public void setActividadesSugeridas(List<ActividadSugerida> a) { this.actividadesSugeridas = a; }

    public static class EvaluacionBienestar {
        private int animo;        // 0-10
        private int sociabilidad; // 0-10
        private int cognitivo;    // 0-10
        private String observacion; // nota breve de Gemini

        public int getAnimo() { return animo; }
        public void setAnimo(int a) { this.animo = a; }

        public int getSociabilidad() { return sociabilidad; }
        public void setSociabilidad(int s) { this.sociabilidad = s; }

        public int getCognitivo() { return cognitivo; }
        public void setCognitivo(int c) { this.cognitivo = c; }

        public String getObservacion() { return observacion; }
        public void setObservacion(String o) { this.observacion = o; }
    }

    public static class ActividadSugerida {
        private String descripcion;
        private boolean confirmadaPorMayor = false;
        private boolean aprobadaPorCuidador = false;
        private String editadaPorCuidador = "";

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String d) { this.descripcion = d; }

        public boolean isConfirmadaPorMayor() { return confirmadaPorMayor; }
        public void setConfirmadaPorMayor(boolean c) { this.confirmadaPorMayor = c; }

        public boolean isAprobadaPorCuidador() { return aprobadaPorCuidador; }
        public void setAprobadaPorCuidador(boolean a) { this.aprobadaPorCuidador = a; }

        public String getEditadaPorCuidador() { return editadaPorCuidador; }
        public void setEditadaPorCuidador(String e) { this.editadaPorCuidador = e; }
    }

    public static class Mensaje {
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