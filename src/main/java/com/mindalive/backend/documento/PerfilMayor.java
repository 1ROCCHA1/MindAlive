package com.mindalive.backend.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Document(collection = "perfiles_mayor")
public class PerfilMayor {

    @Id
    private String id;

    private Long mayorId;

    // Capa 1: permanente, solo la toca el cuidador
    private CapaPermanente capaPermanente;

    // Capa 2: rasgos confirmados 3+ veces por Gemini
    private List<RasgoEstable> capaRasgosEstables = new ArrayList<>();

    // Capa 3: estado reciente, máx 150 palabras, se reescribe
    private String capaEstadoReciente = "";

    private LocalDateTime ultimaActualizacion;

    public static class CapaPermanente {
        private String descripcionCuidador = "";
        private String caracter = "";
        private String limitacionesFisicas = "";
        private List<String> temasQueLeGustan = new ArrayList<>();
        private List<String> temasTabu = new ArrayList<>();
        private String familia = "";
        private boolean tieneProblemasAuditivos = false;
        private boolean seIrritaSiSeLeTratatComoInutil = false;

        public String getDescripcionCuidador() { return descripcionCuidador; }
        public void setDescripcionCuidador(String d) { this.descripcionCuidador = d; }
        public String getCaracter() { return caracter; }
        public void setCaracter(String c) { this.caracter = c; }
        public String getLimitacionesFisicas() { return limitacionesFisicas; }
        public void setLimitacionesFisicas(String l) { this.limitacionesFisicas = l; }
        public List<String> getTemasQueLeGustan() { return temasQueLeGustan; }
        public void setTemasQueLeGustan(List<String> t) { this.temasQueLeGustan = t; }
        public List<String> getTemasTabu() { return temasTabu; }
        public void setTemasTabu(List<String> t) { this.temasTabu = t; }
        public String getFamilia() { return familia; }
        public void setFamilia(String f) { this.familia = f; }
        public boolean isTieneProblemasAuditivos() { return tieneProblemasAuditivos; }
        public void setTieneProblemasAuditivos(boolean t) { this.tieneProblemasAuditivos = t; }
        public boolean isSeIrritaSiSeLeTratatComoInutil() { return seIrritaSiSeLeTratatComoInutil; }
        public void setSeIrritaSiSeLeTratatComoInutil(boolean s) { this.seIrritaSiSeLeTratatComoInutil = s; }
    }

    public static class RasgoEstable {
        private String rasgo = "";
        private int confirmaciones = 1;
        private LocalDateTime ultimaVez;

        public String getRasgo() { return rasgo; }
        public void setRasgo(String r) { this.rasgo = r; }
        public int getConfirmaciones() { return confirmaciones; }
        public void setConfirmaciones(int c) { this.confirmaciones = c; }
        public LocalDateTime getUltimaVez() { return ultimaVez; }
        public void setUltimaVez(LocalDateTime u) { this.ultimaVez = u; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Long getMayorId() { return mayorId; }
    public void setMayorId(Long mayorId) { this.mayorId = mayorId; }
    public CapaPermanente getCapaPermanente() { return capaPermanente; }
    public void setCapaPermanente(CapaPermanente c) { this.capaPermanente = c; }
    public List<RasgoEstable> getCapaRasgosEstables() { return capaRasgosEstables; }
    public void setCapaRasgosEstables(List<RasgoEstable> r) { this.capaRasgosEstables = r; }
    public String getCapaEstadoReciente() { return capaEstadoReciente; }
    public void setCapaEstadoReciente(String e) { this.capaEstadoReciente = e; }
    public LocalDateTime getUltimaActualizacion() { return ultimaActualizacion; }
    public void setUltimaActualizacion(LocalDateTime u) { this.ultimaActualizacion = u; }
}