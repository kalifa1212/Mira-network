package com.hforge.repository;

import com.hforge.model.Comment;
import com.hforge.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findByVideo(Video video, Pageable pageable);
}
