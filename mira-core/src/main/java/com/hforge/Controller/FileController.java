package com.hforge.Controller;

import com.hforge.Controller.Api.FileApi;
import com.hforge.Dto.MosqueDto;
import com.hforge.Dto.PredicationDto;
import com.hforge.Dto.UtilisateurDto;
import com.hforge.Exceptions.ErrorCodes;
import com.hforge.Exceptions.InvalidEntityException;
import com.hforge.Model.Context;
import com.hforge.Services.PredicationService;
import com.hforge.Services.Strategy.Documents.StrategyPhotoContext;
import com.hforge.services.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@Slf4j
public class FileController implements FileApi {
    private StrategyPhotoContext strategyPhotoContext;
    private PredicationService predicationService;
    private FileService fileService;

    @Autowired
    public FileController(StrategyPhotoContext strategyPhotoContext,FileService fileService,
                          PredicationService predicationService){
        this.strategyPhotoContext=strategyPhotoContext;
        this.predicationService=predicationService;
        this.fileService=fileService;
    }

    @Override
    public Object savePhoto(Context context, Integer id, MultipartFile multipartFile) throws IOException, InterruptedException {
        return strategyPhotoContext.savePhoto(context,id,multipartFile);
    }

    @Override
    public Object uploadDataBase( Context context, MultipartFile multipartFile) throws IOException {
        return null;
    }

    @Override
    public ResponseEntity<Resource> getFile(Integer id, Context context) {

        if (context == null) {
            throw new InvalidEntityException(
                    "Le contexte ne peut pas être null",
                    ErrorCodes.UNKNOW_CONTEXT
            );
        }

        Resource resource;
        String fileName;
        String disposition = "inline";

        switch (context) {

            case user -> {
                UtilisateurDto utilisateur =
                        (UtilisateurDto) strategyPhotoContext.displayPhoto(id, context.name());

                resource = fileService.getResource(utilisateur.getImageUrl());
                fileName = "profile";
            }

            case mosque -> {
                MosqueDto mosque =
                        (MosqueDto) strategyPhotoContext.displayPhoto(id, context.name());

                resource = fileService.getResource(mosque.getPhoto());
                fileName = "mosque";
            }

            case preche -> {
                PredicationDto predication =
                        (PredicationDto) strategyPhotoContext.displayPhoto(id, context.name());

                resource = fileService.getResource(predication.getRessourceUrl());
                fileName = "preche";

                // Une prédication peut être téléchargée plutôt qu'affichée
                disposition = "attachment";
            }

            default -> throw new InvalidEntityException(
                    "Contexte inconnu : " + context,
                    ErrorCodes.UNKNOW_CONTEXT
            );
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        disposition + "; filename=\"" + fileName + "\""
                )
                .body(resource);
    }

    @Override
    public ResponseEntity<Resource> streamRessource(Integer predicationId, Context context, String rangeHeader) throws IOException {
        PredicationDto predicationDto=predicationService.findById(predicationId);

        //        Video video = videoService.findVideo(videoId);
        File ressourceFile = new File(predicationDto.getRessourceUrl());
        long fileSize = ressourceFile.length();
        String mimeType = predicationDto.getMimeType();
//
//        // 2. Valeurs par défaut
        long fileLength = ressourceFile.length();
        long start = 0;
        long end = fileLength - 1;
//
//        // 3. Parse du header Range (si présent)
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.substring(6).split("-");
            try {
                start = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]);
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).build();
            }
        }

//        // 4. Ajustement si end dépasse la taille du fichier
        end = Math.min(end, fileLength - 1);
        long contentLength = end - start + 1;

        // 5. Lire uniquement le segment demandé
        InputStream inputStream = new BufferedInputStream(new FileInputStream(ressourceFile));
        inputStream.skip(start);
        InputStream limitedStream = new LimitedInputStream(inputStream, contentLength);

//        // 6. Headers HTTP pour le streaming
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, mimeType);
        headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
        headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
        headers.set(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", start, end, fileLength));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline"); // Pour éviter le téléchargement

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .body(new InputStreamResource(limitedStream));
        //return null;
    }
    public static class LimitedInputStream extends FilterInputStream {
        private long remaining;

        public LimitedInputStream(InputStream in, long limit) {
            super(in);
            this.remaining = limit;
        }
    }

    public Resource uploadingFile(MosqueDto mosqueDto){
        Path path = Paths.get(mosqueDto.getPhoto());
        Resource resource = null;
        try {
            resource = (Resource) new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return  resource;
    } public Resource uploadingGeneralObjetFule(PredicationDto object){
        Path path = Paths.get(object.getFichier());
        Resource resource = null;
        try {
            resource = (Resource) new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return  resource;
    }
    public Resource uploadingFileU(UtilisateurDto utilisateurDto){
        //log.error("test 2 {}",utilisateurDto);
        Path path = Paths.get(utilisateurDto.getImageUrl());
        Resource resource = null;
        try {
            resource = (Resource) new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return  resource;
    }

}
