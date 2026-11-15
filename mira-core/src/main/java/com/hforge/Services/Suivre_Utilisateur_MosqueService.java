package com.hforge.Services;

import com.hforge.Dto.SuivreDto;
import com.hforge.Model.Suivre;

import java.util.List;

public interface Suivre_Utilisateur_MosqueService {
    SuivreDto save(SuivreDto  dto);
    List<Suivre> findAll();
    void delete(Integer id);
}
