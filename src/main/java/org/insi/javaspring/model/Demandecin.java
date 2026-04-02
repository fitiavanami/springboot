package org.insi.javaspring.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "demande_cin")
public class Demandecin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroDossier;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le type de demande est obligatoire")
    @Column(nullable = false)
    private Type demande;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le statut est obligatoire")
    @Column(nullable = false)
    private Etat status;

    @Column(nullable = false)
    private LocalDate dateDepot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citoyen_id", nullable = false)
    private Citoyen citoyen;

    @Column(nullable = false)
    private String agentResponsable;
}