package com.hforge.Dto;

import com.hforge.Model.Suivre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class SuivreDto {

    private Integer id;

    private Integer idimamsuivie;

    private Integer utilisateur;

    private Integer mosque;

//    private UtilisateurDto user;
//
//    private MosqueDto mosq;

    public static SuivreDto fromEntity(Suivre suivre) {
        if(suivre==null) {
            return null;
        }
        return SuivreDto.builder()
                .id(suivre.getId())
                .idimamsuivie(suivre.getIdimamsuivie())
                .utilisateur(suivre.getUtilisateur())
                .mosque(suivre.getMosque())
//                .mosq(MosqueDto.fromEntity(suivre.getMosq()))
//                .user(UtilisateurDto.fromEntity(suivre.getUser()))
                .build();
    }

    public static Suivre toEntity(SuivreDto suivreDto) {
        if(suivreDto==null) {
            return null;
        }
        Suivre suivre = new Suivre();
        suivre.setId(suivreDto.getId());
        suivre.setUtilisateur(suivreDto.getUtilisateur());
        suivre.setMosque(suivreDto.getMosque());
        suivre.setIdimamsuivie(suivreDto.getIdimamsuivie());
//        suivre.setUser(UtilisateurDto.toEntity(suivreDto.getUser()));
//        suivre.setMosq(MosqueDto.toEntity(suivreDto.getMosq()));
        return suivre;
    }
}
