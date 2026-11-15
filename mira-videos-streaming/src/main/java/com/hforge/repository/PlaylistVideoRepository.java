package com.hforge.repository;

import com.hforge.model.PlaylistVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlaylistVideoRepository extends JpaRepository<PlaylistVideo, UUID> {
}
