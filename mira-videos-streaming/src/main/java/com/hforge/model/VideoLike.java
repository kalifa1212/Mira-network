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
 * Like d'une vidéo. Table dédiée (plutôt qu'un Like polymorphe
 * target_type/target_id) afin de garder de vraies FK et une contrainte
 * d'unicité fiable en PostgreSQL.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "video_likes", uniqueConstraints = @UniqueConstraint(name = "uk_video_like_user", columnNames = {"video_id", "user_id"}))
public class VideoLike extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private Utilisateur utilisateur;
}
