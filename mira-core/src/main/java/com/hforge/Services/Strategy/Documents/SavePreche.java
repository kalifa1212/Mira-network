package com.hforge.Services.Strategy.Documents;

import com.hforge.Dto.PredicationDto;
import com.hforge.Dto.RessourceMetadata;
import com.hforge.Model.Predication;
import com.hforge.Services.File.LocalFileService;
import com.hforge.Services.PredicationService;
import com.hforge.model.FileInfo;
import com.hforge.services.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service("precheStrategy")
public class SavePreche implements StrategyPhoto<PredicationDto> {

    private PredicationService predicationService;
    @Value("${project.poster}")
    String FilePath;
    @Autowired
    private LocalFileService localFileService;
    private FileService fileService;

    public SavePreche(PredicationService predicationService,FileService fileService){
        this.predicationService=predicationService;
        this.fileService=fileService;
    }
    @Override
    public PredicationDto savePhoto(Integer id, MultipartFile multipartFile) throws IOException, InterruptedException {
        FileInfo predicationFile=fileService.upload(multipartFile);
        PredicationDto predi=predicationService.findById(id);
        Predication predication=PredicationDto.toEntity(predi);
        predication.setMimeType(predicationFile.getMimeType());
        predication.setRessourceUrl(predicationFile.getStoragePath());

//        PredicationDto predi=predicationService.findById(id);
//        Predication predication=PredicationDto.toEntity(predi);
//        String ressourceUrl = saveVideoFile(multipartFile,id,predication.getType().toString().toLowerCase());
//        Path filePath = new File(ressourceUrl).toPath();
//        predication.setMimeType(getMimeType(filePath));
//        predication.setRessourceUrl(ressourceUrl);

        if(predication.getIdImam()==null||predication.getIdMosque()==null){
            predication.setIdMosque(0);
            predication.setIdImam(0);
        }
        log.info("Predication save");
        return predicationService.save(PredicationDto.fromEntity(predication));
    }
    private String saveVideoFile(MultipartFile file,Integer id,String context) {
        // Code pour sauvegarder la vidéo (local, cloud, etc.)

        return localFileService.saveFile(file,id.toString(),context);
    }
    public String getMimeType(Path filePath) throws IOException {
        return Files.probeContentType(filePath); // ex: "video/mp4"
    }
    public RessourceMetadata extractMetadata(MultipartFile file) {
        // Utilise JCodec ou FFmpeg pour lire la vidéo
        // Retourne durée, résolution, MIME type, taille
        RessourceMetadata metadata =new RessourceMetadata();
        return metadata;
    }
    @Override
    public PredicationDto downloadPhoto(Integer id) {

        return predicationService.findById(id);
    }
}
