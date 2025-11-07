package com.lifelibrarians.lifebookshelf.autobiography.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lifelibrarians.lifebookshelf.autobiography.validate.ChapterCreateValidation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@Getter
@Schema(description = "자서전 챕터 생성 요청")
@ToString
@FieldNameConstants
@ChapterCreateValidation
public class ChapterCreateRequestDto {

	@ArraySchema(schema = @Schema(implementation = ChapterRequestDto.class))
	private final List<ChapterRequestDto> chapters;

	@JsonCreator
	public ChapterCreateRequestDto(@JsonProperty("chapters") List<ChapterRequestDto> chapters) {
		this.chapters = chapters;
	}
}