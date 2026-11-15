package com.hforge.repository;

import com.hforge.Model.Utilisateur;
import com.hforge.model.Video;
import com.hforge.model.VideoLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VideoLikeRepository extends JpaRepository<VideoLike, UUID> {
    Optional<VideoLike> findByUtilisateurAndVideo(Utilisateur utilisateur,
                                                  Video video);
}
