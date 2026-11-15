package com.hforge.Services.Impl;

import com.hforge.Dto.DocumentsDto;
import com.hforge.Dto.PredicationDto;
import com.hforge.Exceptions.EntityNotFoundException;
import com.hforge.Exceptions.ErrorCodes;
import com.hforge.Model.Documents;
import com.hforge.Repository.DocumentsRepository;
import com.hforge.Repository.PredicationRepository;
import com.hforge.Services.DocumentsService;
import com.hforge.Services.File.FileUpload;
import com.hforge.Services.PredicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.Optional;

@Service
@Slf4j
public class DocumentsServiceImpl implements DocumentsService {
	
	private DocumentsRepository documentsRepository;
	private PredicationService predicationService;
	private PredicationRepository predicationRepository;

	@Value("${project.poster}")
	String path;
	
	@Autowired
	public DocumentsServiceImpl(
			DocumentsRepository documentsRepository,
			PredicationRepository predicationRepository,
			PredicationService predicationService
	) {
		this.documentsRepository=documentsRepository;
		this.predicationService=predicationService;
		this.predicationRepository=predicationRepository;
	}
	
	@Override
	public DocumentsDto save(Integer idPredication, MultipartFile multipartFile) throws IOException {
		// TODO Auto-generated method stub
		PredicationDto predication=predicationService.findById(idPredication);
		DocumentsDto dto=new DocumentsDto();
		if(predication==null){
			throw new InvalidClassException("aucune predicaiton n'existe avec l'id donnée");
		}
		String FileName= StringUtils.cleanPath(multipartFile.getOriginalFilename());
		String extension=FileName.substring(FileName.lastIndexOf(".")+1);
		String uploadDir=path+"predication/"+predication.getType()+"/";
		dto.setType_doc(multipartFile.getContentType());
		dto.setNom(predication.getTheme()+"-"+predication.getDate());
		dto.setPredication(predication);
		FileName=predication.getId()+"."+extension;
		dto.setFichier(uploadDir+FileName);
		dto=DocumentsDto.fromEntity(documentsRepository.save(DocumentsDto.toEntity(dto)));
		FileUpload.saveFile(uploadDir,FileName,multipartFile);
		return dto;
	}

	@Override
	public DocumentsDto findById(Integer id) {
		// TODO Auto-generated method stub
		if(id==null) {
			log.error("l'id de la documents est null");
			return null;
		}
		Optional<Documents> documents= documentsRepository.findById(id);
		
		return Optional.of(DocumentsDto.fromEntity(documents.get())).orElseThrow(() ->
				new EntityNotFoundException(
						"Aucune documents avec l'id ="+id+"n'a ete trouver dans la BD",
						ErrorCodes.MOSQUE_NOT_FOUND)
		);
	}

	@Override
	public DocumentsDto findByPredication(Integer id) {
		if(id==null) {
			log.error("l'id de la documents est null");
			return null;
		}
		Documents documents= documentsRepository.findDocumentsByPredicationId(id);

		return Optional.of(DocumentsDto.fromEntity(documents)).orElseThrow(() ->
				new EntityNotFoundException(
						"Aucune documents avec l'id ="+id+"n'a ete trouver dans la BD",
						ErrorCodes.MOSQUE_NOT_FOUND)
		);
	}

	@Override
	public Page<DocumentsDto> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return documentsRepository.findAll(pageable)
				.map(DocumentsDto::fromEntity);
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		if(id==null) {
			log.error("l'id de la documents est null");
			return ;
		}
		
		documentsRepository.deleteById(id);
	}

}
