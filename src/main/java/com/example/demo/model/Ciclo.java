package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ciclos")
public class Ciclo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TipoCiclo nombre;

    public enum TipoCiclo {
        CICLO_VERANO("Ciclo Verano"),
        CICLO_ANUAL("Ciclo Anual"),
        CICLO_SEMESTRAL("Ciclo Semestral"),
        CICLO_REPASO_INTENSIVO("Ciclo Intensivo");

        private final String nombre;

        TipoCiclo(String nombre) {
            this.nombre = nombre;
        }

        public String getNombre() {
            return nombre;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoCiclo getNombre() { return nombre; }
    public void setNombre(TipoCiclo nombre) { this.nombre = nombre; }
}