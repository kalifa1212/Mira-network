package com.hforge.Dto;

import com.hforge.Model.Utilisateur;
import com.hforge.model.*;
import com.hforge.model.enums.VideoStatus;
import com.hforge.model.enums.VideoVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder  @AllArgsConstructor
@NoArgsConstructor
public class VideoDto {

    UUID id;

    String title;

    String description;

    String thumbnailUrl;

    String streamUrl;

    Long views;

    Long likes;

    Long comments;


    Boolean liked;


    String author;

    public static VideoDto fromEntity(Video video) {
       return null;
    }
    public static Video toEntity(VideoDto videoDto) {
        return null;
    }
}
