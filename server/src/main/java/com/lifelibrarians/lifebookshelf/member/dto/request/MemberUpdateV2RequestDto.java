package com.lifelibrarians.lifebookshelf.member.dto.request;

import com.lifelibrarians.lifebookshelf.member.domain.GenderType;
import com.lifelibrarians.lifebookshelf.member.validate.MemberUpdateV2Validation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "회원 메타데이터 수정 요청 DTO V2")
@ToString
@FieldNameConstants
@MemberUpdateV2Validation
public class MemberUpdateV2RequestDto {

	@Schema(description = "성별", example = "MALE")
	private final GenderType gender;

	@Schema(description = "주제", example = "가족을 위한 기록")
	private final String theme;

	@Schema(description = "연령대", example = "30")
	private final String ageGroup;

	@Schema(description = "직업군", example = "IT")
	private final String job;

	@Schema(description = "생성목적", example = "추억 보존")
	private final String whyCreate;
}