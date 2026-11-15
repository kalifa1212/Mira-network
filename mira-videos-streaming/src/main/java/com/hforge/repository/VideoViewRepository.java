package com.hforge.repository;

import com.hforge.Model.Utilisateur;
import com.hforge.model.Video;
import com.hforge.model.VideoView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VideoViewRepository extends JpaRepository<VideoView, UUID> {
    boolean existsByVideoAndUtilisateur(Video video, Utilisateur utilisateur);
}
