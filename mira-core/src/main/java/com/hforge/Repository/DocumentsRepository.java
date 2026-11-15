package com.hforge.Repository;


import com.hforge.Model.Documents;
import com.hforge.Model.Predication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentsRepository extends JpaRepository<Documents,Integer> {
    Page<Documents> findAll(Pageable pageable);
    Documents findDocumentsByPredicationId(Integer id);
    Documents findDocumentsByPredication(Predication predication);

}
