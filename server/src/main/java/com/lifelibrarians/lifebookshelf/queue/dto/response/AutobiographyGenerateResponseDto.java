package com.lifelibrarians.lifebookshelf.queue.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AutobiographyGenerateResponseDto {
    private Long autobiographyId;
    private Long userId;

    private String title;
    private String content;
}
