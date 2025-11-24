package com.lifelibrarians.lifebookshelf.queue.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AutobiographyGenerateResponseDto {
    private String cycleId;
    
    private Integer step;

    private Long autobiographyId;

    private Long userId;
    
    private String title;
    private String content;
    private String action;
}
