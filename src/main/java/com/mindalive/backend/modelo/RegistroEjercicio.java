package com.mindalive.backend.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registros_ejercicios")
public class RegistroEjercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long mayorId;

    private String tipoJuego; // SIMON, MEMORY, OPERACIONES, ELEMENTO_DISTINTO

    private int nivel;

    private int aciertos;

    private int errores;

    private double tiempoMedioRespuesta; // en segundos

    private boolean subioPorNivel;

    private boolean bajoPorNivel;

    private LocalDateTime fecha;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMayorId() { return mayorId; }
    public void setMayorId(Long mayorId) { this.mayorId = mayorId; }

    public String getTipoJuego() { return tipoJuego; }
    public void setTipoJuego(String tipoJuego) { this.tipoJuego = tipoJuego; }

    public int getNivel() { return nivel; }
    public void setNivel(int nivel) { this.nivel = nivel; }

    public int getAciertos() { return aciertos; }
    public void setAciertos(int aciertos) { this.aciertos = aciertos; }

    public int getErrores() { return errores; }
    public void setErrores(int errores) { this.errores = errores; }

    public double getTiempoMedioRespuesta() { return tiempoMedioRespuesta; }
    public void setTiempoMedioRespuesta(double tiempoMedioRespuesta) { this.tiempoMedioRespuesta = tiempoMedioRespuesta; }

    public boolean isSubioPorNivel() { return subioPorNivel; }
    public void setSubioPorNivel(boolean subioPorNivel) { this.subioPorNivel = subioPorNivel; }

    public boolean isBajoPorNivel() { return bajoPorNivel; }
    public void setBajoPorNivel(boolean bajoPorNivel) { this.bajoPorNivel = bajoPorNivel; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}