package com.hforge.model;

import com.hforge.Model.Utilisateur;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Abonnement d'un utilisateur (abonne) à la chaîne d'un autre utilisateur (chaine).
 * Ajoutez en migration SQL : CHECK (abonne_id <> chaine_id) — non exprimable
 * proprement via une annotation JPA standard.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "subscriptions", uniqueConstraints = @UniqueConstraint(name = "uk_subscription", columnNames = {"abonne_id", "chaine_id"}))
public class Subscription extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "abonne_id", nullable = false, updatable = false)
    private Utilisateur abonne;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chaine_id", nullable = false, updatable = false)
    private Utilisateur chaine;
}
