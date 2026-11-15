package com.hforge.model;

import com.hforge.Model.Utilisateur;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Historique de visionnage : alimente les stats, les recommandations et le "reprendre la lecture". */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "video_views", indexes = {
        @Index(name = "idx_video_views_video_id", columnList = "video_id"),
        @Index(name = "idx_video_views_user_id", columnList = "user_id")
})
public class VideoView extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    // Nullable : autorise le suivi des vues pour un visiteur anonyme
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Utilisateur utilisateur;

    @Column(name = "duree_vue_secondes")
    private Integer dureeVueSecondes;
}
