package com.monika.worek.orchestra.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MusicScoreDTO {

    private Long id;
    private String fileName;
    private String fileType;
    private String downloadUrl;
}
