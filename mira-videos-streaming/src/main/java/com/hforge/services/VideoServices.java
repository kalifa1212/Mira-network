package com.hforge.services;

import com.hforge.Dto.CommentRequest;
import com.hforge.Dto.UploadResponse;
import com.hforge.Dto.VideoDto;
import com.hforge.Dto.ViewRequest;
import com.hforge.model.Comment;
import com.hforge.model.Video;
import com.hforge.model.VideoLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public interface VideoServices {

    UploadResponse upload(MultipartFile video, String title, String description, String visibility, Integer UserId) throws IOException, InterruptedException;
    List<Video> videoList();
    Page<VideoDto> videoFeed(Pageable pageable);
    VideoDto getVideoById(UUID videoId);
    Video getVideo(UUID videoId);
    void addView(UUID videoId, ViewRequest viewRequest);
    boolean likeVideo(UUID videoId,Integer userId);
    VideoLike likeVideoStatus(UUID videoId, Integer userId);
    boolean unLikeVideo(UUID videoId);
    Comment addComment(UUID videoId , CommentRequest commentRequest);
    Page<Comment> getComment(UUID videoId,Pageable pageable);
    boolean deleteComment(UUID commentId);
    void shareVideo(UUID videoId);

    Path getGlobalPath(String pathFile);
}
