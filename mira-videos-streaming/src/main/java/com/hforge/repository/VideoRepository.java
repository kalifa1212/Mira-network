package com.hforge.repository;

import com.hforge.enums.Visibility;
import com.hforge.model.Video;
import com.hforge.model.enums.VideoVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VideoRepository extends JpaRepository<Video, UUID> {
    Page<Video> findByVisibilite(VideoVisibility visibility, Pageable pageable);
}
