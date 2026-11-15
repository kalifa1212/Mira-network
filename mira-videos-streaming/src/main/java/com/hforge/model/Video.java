package com.hforge.model;

import com.hforge.Model.Utilisateur;
import com.hforge.model.enums.VideoStatus;
import com.hforge.model.enums.VideoVisibility;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "videos", indexes = {
        @Index(name = "idx_videos_user_id", columnList = "user_id"),
        @Index(name = "idx_videos_categorie_id", columnList = "categorie_id"),
        @Index(name = "idx_videos_statut", columnList = "statut")
})
public class Video extends BaseEntity {

    // Entité User portée par le module core : couplage volontairement fort
    // ici via @ManyToOne. Si vous préférez découpler streaming de core,
    // remplacez par un simple champ `UUID userId` + validation applicative.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private Utilisateur utilisateur;

    @Column(nullable = false, length = 150)
    private String titre;

    //@Lob
    @Column(nullable = false)
    private String description;

    @Column(name = "url_video", nullable = false, length = 500)
    private String urlVideo;

    @Column(name = "url_miniature", length = 500)
    private String urlMiniature;

    @Column(name = "duree_secondes")
    private Integer dureeSecondes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private VideoStatus statut = VideoStatus.UPLOADING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private VideoVisibility visibilite = VideoVisibility.PRIVATE;

    // Compteur dénormalisé : à incrémenter via job async ou trigger SQL,
    // ne jamais faire un COUNT(*) sur video_views à l'affichage.
    @Column(name = "nb_vues", nullable = false)
    @Builder.Default
    private Long nbVues = 0L;

    //@Column(name = "nb_likes", nullable = false)
    @Column(name = "nb_likes")
    @Builder.Default
    private Long nbLikes = 0L;


   // @Column(name = "nb_commentaires", nullable = false)
    @Column(name = "nb_commentaires")
    @Builder.Default
    private Long nbCommentaires = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id")
    private Category categorie;

    @Builder.Default
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Comment> commentaires = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<VideoLike> likes = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "video", fetch = FetchType.LAZY)
    private Set<VideoView> vues = new HashSet<>();
}
