package com.mindalive.backend.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "vinculos_familiares")
public class VinculoFamiliar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mayor_id")
    private Usuario mayor;

    @ManyToOne
    @JoinColumn(name = "familiar_id")
    private Usuario familiar;

    private String permiso;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getMayor() { return mayor; }
    public void setMayor(Usuario mayor) { this.mayor = mayor; }

    public Usuario getFamiliar() { return familiar; }
    public void setFamiliar(Usuario familiar) { this.familiar = familiar; }

    public String getPermiso() { return permiso; }
    public void setPermiso(String permiso) { this.permiso = permiso; }
}