package com.hforge.Services;


import com.hforge.Dto.DocumentsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DocumentsService {

	DocumentsDto save(Integer idPredication, MultipartFile multipartFile)throws IOException;

//	int countAll();
//	int countAllByTypeDoc(String typedoc);
	DocumentsDto findById(Integer id);
	DocumentsDto findByPredication(Integer id);
	Page<DocumentsDto> findAll(Pageable pageable);
	void delete(Integer id);
}
