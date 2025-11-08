package com.lifelibrarians.lifebookshelf.member.dto.request;

import com.lifelibrarians.lifebookshelf.member.validate.MemberProfileUpdateValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "회원 프로필 정보 수정 요청 DTO")
@ToString
@FieldNameConstants
@MemberProfileUpdateValidation
public class MemberProfileUpdateRequestDto {

	@Schema(description = "닉네임", example = "시워언해")
	private final String nickname;

	@Schema(description = "사전지정 프로필 이미지 URL", example = "profile-images/random-string/image.jpg")
	private String preSignedProfileImageUrl;
}
