package com.lifelibrarians.lifebookshelf.autobiography.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lifelibrarians.lifebookshelf.autobiography.validate.AutobiographyCreateValidation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@Getter
@Schema(description = "자서전 생성 요청 DTO")
@ToString
@FieldNameConstants
@AutobiographyCreateValidation
public class AutobiographyCreateRequestDto {

	@Schema(description = "자서전 제목", example = "My New Autobiography")
	private final String title;

	@Schema(description = "자서전 내용", example = "This is the content of my new autobiography.")
	private final String content;

	@Schema(description = "사전지정 커버 이미지 URL", example = "bio-cover-images/random-string/image.png")
	private final String preSignedCoverImageUrl;

	@ArraySchema(schema = @Schema(implementation = InterviewQuestionRequestDto.class))
	private final List<InterviewQuestionRequestDto> interviewQuestions;

	@JsonCreator
	public AutobiographyCreateRequestDto(String title, String content,
			String preSignedCoverImageUrl, List<InterviewQuestionRequestDto> interviewQuestions) {
		this.title = title;
		this.content = content;
		this.preSignedCoverImageUrl = preSignedCoverImageUrl;
		this.interviewQuestions = interviewQuestions;
	}
}
