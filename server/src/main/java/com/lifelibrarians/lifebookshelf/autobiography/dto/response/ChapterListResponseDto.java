package com.lifelibrarians.lifebookshelf.autobiography.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "자서전 챕터 목록 응답")
@ToString
public class ChapterListResponseDto {

	@Schema(description = "현재 챕터 ID", example = "3")
	private final Long currentChapterId;

	@ArraySchema(schema = @Schema(implementation = ChapterDto.class))
	private final List<ChapterDto> results;
}