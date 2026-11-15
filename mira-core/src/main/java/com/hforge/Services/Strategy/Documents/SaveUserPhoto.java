package com.hforge.Services.Strategy.Documents;

import com.hforge.Dto.UtilisateurDto;
import com.hforge.Exceptions.EntityNotFoundException;
import com.hforge.Services.File.LocalFileService;
import com.hforge.Services.File.FileUpload;
import com.hforge.Services.UtilisateurService;
import com.hforge.model.FileInfo;
import com.hforge.services.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service("userStrategy")
public class SaveUserPhoto implements StrategyPhoto{

    private UtilisateurService utilisateurService;
    private LocalFileService localFileService;
    private FileService fileService;

    @Value("${project.poster}")
    String path;

    public SaveUserPhoto(UtilisateurService utilisateurService, LocalFileService localFileService, FileService fileService){
        this.utilisateurService=utilisateurService;
        this.localFileService = localFileService;
        this.fileService=fileService;
    }
    @Override
    public Object savePhoto(Integer id, MultipartFile multipartFile) throws IOException, InterruptedException {

        UtilisateurDto utilisateur = utilisateurService.findById(id);
        if(utilisateur.equals(null)){
            throw new EntityNotFoundException("Aucun utilisateur pour l'id entrer");
        }
        FileInfo userPicture=fileService.upload(multipartFile);
        utilisateur.setImageUrl(userPicture.getStoragePath());
        utilisateur.setImagedata(null);

//        String FileName= StringUtils.cleanPath(multipartFile.getOriginalFilename());
//        String extension=FileName.substring(FileName.lastIndexOf(".")+1);
//        String uploadDir=path+"utilisateur/";
//        FileName=utilisateur.getId()+"."+extension;
//        utilisateur.setImageUrl(uploadDir+FileName);
//        utilisateur.setImagedata(null);
//        FileUpload.saveFile(uploadDir,FileName,multipartFile);
        //log.error("user saving 1  {} ",utilisateur);
        //MosqueDto retour = MosqueDto.fromEntity( mosqueRepository.save(MosqueDto.toEntity(mosque)));

        boolean update=true;
        return utilisateurService.save(utilisateur,update);
    }

    @Override
    public Object downloadPhoto(Integer id) {
        UtilisateurDto utilisateurDto = utilisateurService.findById(id);
        return utilisateurDto;
    }
}
