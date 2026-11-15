package com.hforge.Model.Mapper;

import com.hforge.Dto.UtilisateurDto;
import com.hforge.Model.Utilisateur;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "default")
public interface UtilisateurMapper {
    UtilisateurDto toDto(Utilisateur entity);

    Utilisateur toEntity(UtilisateurDto dto);

    List<UtilisateurDto> toDtoList(List<Utilisateur> entities);

    List<Utilisateur> toEntityList(List<UtilisateurDto> dtos);
}
