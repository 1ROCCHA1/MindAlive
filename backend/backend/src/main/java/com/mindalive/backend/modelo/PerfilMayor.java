package com.mindalive.backend.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

// Información adicional específica del mayor
@Entity
@Table(name = "perfil_mayor")
public class PerfilMayor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private LocalDate fechaNacimiento;

    private String notasMedicas;

    // Porcentaje de bajada que dispara una alerta
    private int umbralAlerta = 20;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getNotasMedicas() { return notasMedicas; }
    public void setNotasMedicas(String notasMedicas) { this.notasMedicas = notasMedicas; }

    public int getUmbralAlerta() { return umbralAlerta; }
    public void setUmbralAlerta(int umbralAlerta) { this.umbralAlerta = umbralAlerta; }
}