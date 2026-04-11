package com.mindalive.backend.modelo;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "recordatorios")
public class Recordatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mayor_id")
    private Usuario mayor;

    @ManyToOne
    @JoinColumn(name = "medicamento_id")
    private Medicamento medicamento;

    private String mensaje;
    private LocalTime horaAviso;

    // Días separados por coma: "LUNES,MIERCOLES,VIERNES"
    private String diasRepeticion;

    private boolean activo = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getMayor() { return mayor; }
    public void setMayor(Usuario mayor) { this.mayor = mayor; }

    public Medicamento getMedicamento() { return medicamento; }
    public void setMedicamento(Medicamento medicamento) { this.medicamento = medicamento; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public LocalTime getHoraAviso() { return horaAviso; }
    public void setHoraAviso(LocalTime horaAviso) { this.horaAviso = horaAviso; }

    public String getDiasRepeticion() { return diasRepeticion; }
    public void setDiasRepeticion(String diasRepeticion) { this.diasRepeticion = diasRepeticion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
