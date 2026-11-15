package com.hforge.Repository;


import com.hforge.Dto.TypePredication;
import com.hforge.Model.Predication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PredicationRepository extends JpaRepository<Predication, Integer> {

	List<Predication> findById(int a);
	//Predication findById(int a);
	Page<Predication> findPredicationByNomImamContainingIgnoreCase(String nomImam,Pageable pageable);
	Page<Predication> findPredicationByThemeContainingIgnoreCase(String str,Pageable pageable);
	Page<Predication> findPredicationByType(TypePredication type, Pageable pageable);
	Page<Predication> findPredicationByIdImam(int a,Pageable pageable);

}
