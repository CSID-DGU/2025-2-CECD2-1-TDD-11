package com.lifelibrarians.lifebookshelf.member.dto.response;

import com.lifelibrarians.lifebookshelf.member.domain.GenderType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "회원 기본 정보 응답 DTO")
@ToString
@FieldNameConstants
public class MemberBasicResponseDto {

	@Schema(description = "회원 이름", example = "John Doe")
	private final String name;

	@Schema(description = "생년월일", example = "2000-01-01")
	private final LocalDate bornedAt;

	@Schema(description = "성별", example = "MALE")
	private final GenderType gender;

	@Schema(description = "자녀 여부", example = "false")
	private final boolean hasChildren;

	@Schema(description = "직업", example = "Software Engineer")
	private final String occupation;

	@Schema(description = "학력", example = "Bachelor's Degree")
	private final String educationLevel;

	@Schema(description = "결혼 여부", example = "미혼")
	private final String maritalStatus;
}
