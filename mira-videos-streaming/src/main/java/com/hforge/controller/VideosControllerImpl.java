package com.hforge.controller;

import com.hforge.Config.Security.CustomUserDetails;
import com.hforge.Dto.CommentRequest;
import com.hforge.Dto.VideoDto;
import com.hforge.Dto.ViewRequest;
import com.hforge.controller.api.VideosController;
import com.hforge.model.Video;
import com.hforge.services.VideoServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class VideosControllerImpl implements VideosController {

    private VideoServices videoServices;

    @Autowired
    VideosControllerImpl(VideoServices videoServices){
        this.videoServices=videoServices;
    }

    @Override
    public ResponseEntity<?> uploadVideo(MultipartFile video, String title, String description, String visibility) throws IOException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = getUserId(authentication);

        //log.info("debut id : {} ",id);
        return ResponseEntity.ok(videoServices.upload(video,title,description,visibility, userId));
    }

    @Override
    public ResponseEntity<Resource> serveHlsFile(String filename, UUID videoId) throws IOException {
        System.out.println("--starting streaming---");
        Video video=videoServices.getVideo(videoId);

        // 1) Nettoyage : {*filename} capture le chemin avec un "/" en préfixe
        String cleanFilename = filename.startsWith("/") ? filename.substring(1) : filename;
        // String cleanPath = videoId.startsWith("/") ? videoId.substring(1) : videoId;

        // 3) Résolution du chemin réel sur disque
       // Path videoDir = fileService.getPath("videos/2026/07/de48ba53-3e32-4241-9e4d-6b40efb6f871/hls");
        Path videoDir = Path.of(video.getUrlVideo());
        videoDir = videoDir.getParent().resolve("hls");

        System.out.println("clean filename "+cleanFilename);

        Path filePath = videoDir.resolve(cleanFilename);
        filePath=videoServices.getGlobalPath(filePath.toString());
        System.out.println("File path with name "+filePath);

        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            System.out.println("File not exist");
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(filePath);
        MediaType contentType = resolveContentType(cleanFilename);
        System.out.println(contentType);
        System.out.println("---------End--------");
        boolean isPlaylist = cleanFilename.endsWith(".m3u8");

        return ResponseEntity.ok()
                .contentType(contentType)
                // Le playlist peut être régénéré (live) -> pas de cache long
                // Les segments, une fois écrits, ne changent plus -> cache long
                .cacheControl(isPlaylist
                        ? CacheControl.noCache()
                        : CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*") // à restreindre en prod
                .body(resource);
    }

    @Override
    public ResponseEntity<?> getVideo(UUID videoId) {
        return ResponseEntity.ok(videoServices.getVideoById(videoId));
    }

    @Override
    public ResponseEntity<Page<VideoDto>> getFeed(Pageable pageable) {

        return ResponseEntity.ok(videoServices.videoFeed(pageable));
    }

    @Override
    public ResponseEntity<Resource> serveHls(UUID videoId, String filename) throws IOException {
        return null;
    }

    @Override
    public ResponseEntity<?> addView(UUID videoId, ViewRequest request) {
        videoServices.addView(videoId,request);
        return ResponseEntity.ok().body("View add");
    }

    @Override
    public ResponseEntity<?> likeVideo(UUID videoId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = getUserId(authentication);

        return ResponseEntity.ok(videoServices.likeVideo(videoId,userId));
    }

    @Override
    public ResponseEntity<?> unlikeVideo(UUID videoId) {
        return null;
    }

    @Override
    public ResponseEntity<?> likeStatus(UUID videoId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = getUserId(authentication);
        return ResponseEntity.ok(videoServices.likeVideoStatus(videoId,userId));
    }

    @Override
    public ResponseEntity<?> addComment(UUID videoId, CommentRequest request) {
        return ResponseEntity.ok(videoServices.addComment(videoId,request));
    }

    @Override
    public ResponseEntity<?> getComments(UUID videoId,Pageable pageable) {
        return ResponseEntity.ok(videoServices.getComment(videoId,pageable));
    }

    @Override
    public ResponseEntity<?> deleteComment(UUID commentId) {
        return ResponseEntity.ok(videoServices.deleteComment(commentId));
    }

    @Override
    public ResponseEntity<?> getVideoStats(UUID videoId) {
        return null;
    }

    @Override
    public ResponseEntity<?> shareVideo(UUID videoId) {
        return null;
    }

    private MediaType resolveContentType(String filename) {
        if (filename.endsWith(".m3u8")) {
            return MediaType.valueOf("application/vnd.apple.mpegurl");
        }
        if (filename.endsWith(".ts")) {
            return MediaType.valueOf("video/mp2t");
        }
        if (filename.endsWith(".m4s")) {
            return MediaType.valueOf("video/iso.segment");
        }
        if (filename.endsWith(".mp4")) {
            return MediaType.valueOf("video/mp4"); // pour un éventuel init segment fMP4
        }
        if (filename.endsWith(".key")) {
            return MediaType.APPLICATION_OCTET_STREAM; // clé de chiffrement HLS (AES-128)
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    Integer getUserId(Authentication authentication){
        Object principal = authentication.getPrincipal() ;
        if (principal instanceof CustomUserDetails customUser) {
            System.out.println("ID trouvé : " + customUser.getId());
           return customUser.getId();
        }
        return null;
    }

}
