package com.hforge.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponse {
    private UUID id;
    private String titre;
    private String description;
    private String urlVideo;
    private String miniature;
    private String categorie;
    private String auteur;
}
