package com.lifelibrarians.lifebookshelf.member.dto.request;

import com.lifelibrarians.lifebookshelf.member.domain.GenderType;
import com.lifelibrarians.lifebookshelf.member.validate.MemberUpdateValidation;
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
@Schema(description = "회원 정보 수정 요청 DTO")
@ToString
@FieldNameConstants
@MemberUpdateValidation
public class MemberUpdateRequestDto {

	@Schema(description = "이름", example = "John Doe")
	private final String name;

	@Schema(description = "생년월일", example = "2000-01-01")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private final LocalDate bornedAt;

	@Schema(description = "성별", example = "MALE")
	private final GenderType gender;

	@Schema(description = "자녀 여부", example = "true")
	private final boolean hasChildren;

	@Schema(description = "직업", example = "Software Engineer")
	private final String occupation;

	@Schema(description = "학력", example = "Bachelor's Degree")
	private final String educationLevel;

	@Schema(description = "결혼 여부", example = "미혼")
	private final String maritalStatus;
}
