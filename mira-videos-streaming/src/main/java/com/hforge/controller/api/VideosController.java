package com.hforge.controller.api;

import com.hforge.Dto.CommentRequest;
import com.hforge.Dto.VideoDto;
import com.hforge.Dto.ViewRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.hforge.Constant.Constants.VIDEOS_STREAMING;

@RequestMapping(VIDEOS_STREAMING)
@SecurityRequirement(name = "Bearer Authentication")
public interface VideosController {
    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> uploadVideo(
            @RequestPart("video") MultipartFile video,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "visibility", defaultValue = "PUBLIC") String visibility
    ) throws IOException, InterruptedException;

    @GetMapping("stream/{videoId}/{filename:.+}")
     ResponseEntity<Resource> serveHlsFile(
            @PathVariable("filename") String filename,
            @PathVariable("videoId") UUID videoId) throws IOException ;

    /**
     * Récupérer une vidéo
     */
    @GetMapping("/{videoId}")
    ResponseEntity<?> getVideo(
            @PathVariable("videoId") UUID videoId
    );



    /**
     * Feed type TikTok
     */
    @GetMapping("/feed")
    ResponseEntity<Page<VideoDto>> getFeed(
            Pageable pageable
    );


    /**
     * Streaming HLS playlist + segments
     *
     * Exemple:
     * /api/videos/stream/id/playlist.m3u8
     * /api/videos/stream/id/segment001.ts
     */
    @GetMapping(
            "/streamgpt/{videoId}/{filename:.+}"
    )
    ResponseEntity<Resource> serveHls(

            @PathVariable("videoId") UUID videoId,

            @PathVariable("filename") String filename

    ) throws IOException;




    /**
     * Ajouter une vue
     */
    @PostMapping("/{videoId}/view")
    ResponseEntity<?> addView(

            @PathVariable("videoId") UUID videoId,

            @RequestBody(required = false) ViewRequest request

    );



    /**
     * Ajouter un like
     */
    @PostMapping("/{videoId}/like")
    ResponseEntity<?> likeVideo(

            @PathVariable("videoId") UUID videoId

    );



    /**
     * Retirer un like
     */
    @DeleteMapping("/{videoId}/unlike")
    ResponseEntity<?> unlikeVideo(

            @PathVariable("videoId") UUID videoId

    );



    /**
     * Vérifier like utilisateur
     */
    @GetMapping("/{videoId}/like/status")
    ResponseEntity<?> likeStatus(

            @PathVariable("videoId") UUID videoId

    );



    /**
     * Ajouter commentaire
     */
    @PostMapping("/{videoId}/comments")
    ResponseEntity<?> addComment(

            @PathVariable("videoId") UUID videoId,

            @RequestBody CommentRequest request

    );



    /**
     * Liste commentaires
     */
    @GetMapping("/{videoId}/comments")
    ResponseEntity<?> getComments(

            @PathVariable("videoId") UUID videoId,
            Pageable pageable

    );



    /**
     * Supprimer commentaire
     */
    @DeleteMapping("/comments/{commentId}")
    ResponseEntity<?> deleteComment(

            @PathVariable("commentId") UUID commentId

    );



    /**
     * Statistiques vidéo
     */
    @GetMapping("/{videoId}/stats")
    ResponseEntity<?> getVideoStats(

            @PathVariable("videoId") UUID videoId

    );



    /**
     * Partage vidéo
     */
    @PostMapping("/{videoId}/share")
    ResponseEntity<?> shareVideo(

            @PathVariable("videoId") UUID videoId

    );
}
