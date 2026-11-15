package com.hforge.services.Impl;

import com.hforge.Dto.CommentRequest;
import com.hforge.Dto.UploadResponse;
import com.hforge.Dto.VideoDto;
import com.hforge.Dto.ViewRequest;
import com.hforge.Exceptions.ErrorCodes;
import com.hforge.Model.Utilisateur;
import com.hforge.Repository.UtilisateurRepository;
import com.hforge.enums.Visibility;
import com.hforge.model.*;
import com.hforge.model.enums.VideoVisibility;
import com.hforge.repository.*;
import com.hforge.services.FileService;
import com.hforge.services.VideoServices;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class VideoServiceImpl implements VideoServices {


    private FileService fileService;
    private VideoRepository videoRepository;
    private UtilisateurRepository utilisateurRepository;
    private CategoryRepository categoryRepository;
    private CommentRepository commentRepository;
    private VideoLikeRepository videoLikeRepository;

    private PlaylistRepository playlistRepository;

    private CommentLikeRepository commentLikeRepository;
    private SubscriptionRepository subscriptionRepository;
    private VideoViewRepository videoViewRepository;

    public VideoServiceImpl(FileService fileService, VideoRepository videoRepository,
                            UtilisateurRepository utilisateurRepository,
                            CategoryRepository categoryRepository, CommentRepository commentRepository,
                            VideoLikeRepository videoLikeRepository,PlaylistRepository playlistRepository,
                            CommentLikeRepository commentLikeRepository,SubscriptionRepository subscriptionRepository,
                            VideoViewRepository videoViewRepository) {
        this.fileService = fileService;
        this.videoRepository=videoRepository;
        this.utilisateurRepository=utilisateurRepository;
        this.categoryRepository=categoryRepository;
        this.commentRepository=commentRepository;
        this.videoLikeRepository=videoLikeRepository;
        this.playlistRepository=playlistRepository;
        this.commentLikeRepository=commentLikeRepository;
        this.subscriptionRepository=subscriptionRepository;
        this.videoViewRepository=videoViewRepository;
    }

    @Override
    public UploadResponse upload(MultipartFile videoFile, String title, String description, String visibility,
                                 Integer userId) throws IOException, InterruptedException {
        FileInfo videoInfo=fileService.upload(videoFile);
        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Utilisateur introuvable : " + userId));

        log.info("Upload d'une vidéo par l'utilisateur {}", userId);

        Category category = categoryRepository.findByNomIs("Imitation")
                .orElseGet(() -> {
                    Category newCategory = Category.builder()
                            .nom("Imitation")
                            .slug("imitation")
                            .build();

                    return categoryRepository.save(newCategory);
                });

        Video video = new Video();

        video.setCategorie(category);
        video.setUrlMiniature(videoInfo.getThumbnailPath());
        video.setUrlVideo(videoInfo.getStoragePath());
        video.setTitre(title);
        video.setDescription(description);
        video.setVisibilite(VideoVisibility.PUBLIC);
        video.setUtilisateur(user);
        log.error(String.valueOf(video));


        Video video2= videoRepository.save(video);
        return UploadResponse.builder()
                .id(video2.getId())
                .titre(video2.getTitre())
                .description(video2.getDescription())
                .categorie(video2.getCategorie().getNom())
                .auteur(video2.getUtilisateur().getNom())
                .build();
    }

    @Override
    public List<Video> videoList() {
        return null;
    }

    @Override
    public Page<VideoDto> videoFeed(Pageable pageable) {
        Page<Video> videos =
                videoRepository.findByVisibilite(
                        VideoVisibility.PUBLIC,
                        pageable
                );


        return videos.map(this::convertToDto);

    }

    @Override
    public VideoDto getVideoById(UUID videoId) {
        Video video=videoRepository.findById(videoId).orElseThrow(()-> new com.hforge.Exceptions.EntityNotFoundException("introuvable",
                ErrorCodes.VIDEO_NOT_FOUND));
        return convertToDto(video);
    }

    @Override
    public Video getVideo(UUID videoId) {
        return videoRepository.findById(videoId).orElseThrow(()-> new com.hforge.Exceptions.EntityNotFoundException("introuvable",
                ErrorCodes.VIDEO_NOT_FOUND));
    }

    @Override
    public void addView(UUID videoId, ViewRequest viewRequest) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vidéo introuvable"));

        Utilisateur utilisateur = utilisateurRepository.findById(viewRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        boolean alreadyViewed = videoViewRepository
                .existsByVideoAndUtilisateur(video, utilisateur);

        if (!alreadyViewed) {

            VideoView view = new VideoView();
            view.setVideo(video);
            view.setUtilisateur(utilisateur);

            videoViewRepository.save(view);

            video.setNbVues(video.getNbVues() + 1);

            videoRepository.save(video);
        }
    }

    //TODO joue le role de like et de unlike
    @Override
    public boolean likeVideo(UUID videoId, Integer userId) {
        Utilisateur user = utilisateurRepository.findById(userId).orElseThrow(()-> new com.hforge.Exceptions.EntityNotFoundException("user not found"));

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vidéo introuvable"));

        Optional<VideoLike> optional =
                videoLikeRepository.findByUtilisateurAndVideo(user, video);

        if(optional.isPresent()) {

            //videoLikeRepository.save(optional.get());
            return false;

        } else {

            VideoLike like = new VideoLike();
            like.setUtilisateur(user);
            like.setVideo(video);

            videoLikeRepository.save(like);
        }

        video.setNbLikes(video.getNbLikes()+1);

        videoRepository.save(video);

        return true;
    }

    @Override
    public VideoLike likeVideoStatus(UUID videoId,Integer userId) {
        Utilisateur user=utilisateurRepository.findById(userId).orElseThrow();
        Video video = videoRepository.findById(videoId)
                .orElseThrow();

        return videoLikeRepository
                .findByUtilisateurAndVideo(user, video)

                .orElseThrow(()-> new com.hforge.Exceptions.EntityNotFoundException("not found"));

    }

    @Override //TODO alrady implement in Like
    public boolean unLikeVideo(UUID videoId) {
        return false;
    }

    @Override
    public Comment addComment(UUID videoId, CommentRequest commentRequest) {

        Utilisateur user = utilisateurRepository.findById(commentRequest.getUserId()).orElseThrow(()-> new com.hforge.Exceptions.EntityNotFoundException(
                "entity not found"
        ));

        Video video = videoRepository.findById(videoId)
                .orElseThrow();

        Comment comment = new Comment();

        comment.setVideo(video);
        comment.setUtilisateur(user);
        comment.setContenu(commentRequest.getComment());

        return commentRepository.save(comment);
    }

    @Override
    public Page<Comment> getComment(UUID videoId,Pageable pageable) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow();

        return commentRepository.findByVideo(video, pageable);
    }



    @Override
    public boolean deleteComment(UUID commentId) {
        return false;
    }

    @Override
    public void shareVideo(UUID videoId) {

    }

    @Override
    public Path getGlobalPath(String pathFile) {
        return fileService.getPath(pathFile);
    }

    private VideoDto convertToDto(Video video){

        return VideoDto.builder()

                .id(video.getId())

                .title(video.getTitre())

                .description(video.getDescription())

                .thumbnailUrl(
                        video.getUrlMiniature()
                )

                .streamUrl(video.getUrlVideo()
                )

                .views(video.getNbVues())

//                .likes(video.getLikes())
//
//                .comments(video.getCommentsCount())
                .author(video.getUtilisateur().getNom())
                .build();
    }
}
