package com.lifelibrarians.lifebookshelf.interview.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lifelibrarians.lifebookshelf.interview.validate.InterviewConversationCreateValidation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@Getter
@Schema(description = "인터뷰 대화 생성 요청 DTO")
@ToString
@FieldNameConstants
@InterviewConversationCreateValidation
public class InterviewConversationCreateRequestDto {

	@ArraySchema(schema = @Schema(implementation = InterviewConversationDto.class))
	private final List<InterviewConversationDto> conversations;

	@JsonCreator
	public InterviewConversationCreateRequestDto(List<InterviewConversationDto> conversations) {
		this.conversations = conversations;
	}
}