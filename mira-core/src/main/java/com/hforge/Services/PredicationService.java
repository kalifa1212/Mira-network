package com.hforge.Services;

import com.hforge.Dto.PredicationDto;
import com.hforge.Dto.TypePredication;
import com.hforge.Model.Predication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;

public interface PredicationService {

	PredicationDto save(PredicationDto dto);
	PredicationDto findById(Integer id);
	Page<Predication> findByImam(String nom,Pageable page);
	Page<PredicationDto> findByThemeImamNom(String str,Pageable page);
	Page<PredicationDto> findByTheme(String str,Pageable page);
	Page<PredicationDto> findByType(TypePredication type, Pageable page);
	Page<PredicationDto> findAll(Pageable page);
	void delete(Integer id);

	void exportData(PrintWriter writer) throws IOException;
	void importDataToDB(MultipartFile file)throws IOException;
}
