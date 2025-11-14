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
@Schema(description = "회원 메타데이터 응답 DTO V2")
@ToString
@FieldNameConstants
public class MemberBasicV2ResponseDto {

	@Schema(description = "회원 이름", example = "홍길동")
	private final String name;

	@Schema(description = "생년월일", example = "1994-01-01")
	private final LocalDate bornedAt;

	@Schema(description = "성별", example = "MALE")
	private final GenderType gender;

	@Schema(description = "자녀 여부", example = "false")
	private final boolean hasChildren;

	@Schema(description = "직업", example = "개발자")
	private final String occupation;

	@Schema(description = "학력", example = "대학교 졸업")
	private final String educationLevel;

	@Schema(description = "결혼 여부", example = "미혼")
	private final String maritalStatus;

	@Schema(description = "주제", example = "가족을 위한 기록")
	private final String theme;

	@Schema(description = "연령대", example = "30대")
	private final String ageGroup;

	@Schema(description = "직업군", example = "IT")
	private final String job;

	@Schema(description = "생성목적", example = "추억 보존")
	private final String whyCreate;
}