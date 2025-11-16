package com.lifelibrarians.lifebookshelf.member.dto.request;

import com.lifelibrarians.lifebookshelf.member.domain.GenderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "회원 정보 수정 요청 DTO V2")
@ToString
@FieldNameConstants
public class MemberUpdateRequestDtoV2 {

	@Schema(description = "성별", example = "MALE")
	private final GenderType gender;

	@Schema(description = "직업", example = "Software Engineer")
	private final String occupation;

	@Schema(description = "연령대", example = "30")
	private final String ageGroup;
}
