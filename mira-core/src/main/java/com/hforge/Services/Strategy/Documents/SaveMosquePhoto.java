package com.hforge.Services.Strategy.Documents;

import com.hforge.Dto.MosqueDto;
import com.hforge.Repository.MosqueRepository;
import com.hforge.Services.File.FileUpload;
import com.hforge.Services.MosqueService;
import com.hforge.model.FileInfo;
import com.hforge.services.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service("mosqueStrategy")
public class SaveMosquePhoto implements StrategyPhoto{

    private MosqueService mosqueService;
    private MosqueRepository mosqueRepository;
    private FileService fileService;

    @Value("${project.poster}")
    String path;
    public SaveMosquePhoto(MosqueService mosqueService,MosqueRepository mosqueRepository,FileService fileService){
        this.mosqueService=mosqueService;
        this.mosqueRepository=mosqueRepository;
        this.fileService=fileService;
    }
    @Override
    public Object savePhoto(Integer id, MultipartFile multipartFile) throws IOException, InterruptedException {

        MosqueDto mosque = mosqueService.findById(id);
        FileInfo uploadMosque=fileService.upload(multipartFile);

        mosque.setPhoto(uploadMosque.getStoragePath());
        mosque.setImagedata(null);
        return MosqueDto.fromEntity( mosqueRepository.save(MosqueDto.toEntity(mosque)));

//        String FileName= StringUtils.cleanPath(multipartFile.getOriginalFilename());
//        String extension=FileName.substring(FileName.lastIndexOf(".")+1);
//        String uploadDir=path+"predication/"+"mosque/";
//        FileName=mosque.getId()+"."+extension;
//        mosque.setPhoto(uploadDir+FileName);
//        mosque.setImagedata(null);
//        FileUpload.saveFile(uploadDir,FileName,multipartFile);
//        MosqueDto retour = MosqueDto.fromEntity( mosqueRepository.save(MosqueDto.toEntity(mosque)));
//        return retour;
    }

    @Override
    public Object downloadPhoto(Integer id) {
        MosqueDto mosqueDto= mosqueService.findById(id);
        return mosqueDto;
    }
}
