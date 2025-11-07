package com.lifelibrarians.lifebookshelf.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "회원 프로필 정보 응답 DTO")
@ToString
@FieldNameConstants
public class MemberProfileResponseDto {

	@Schema(description = "회원 ID", example = "1")
	private final Long memberId;

	@Schema(description = "회원 닉네임", example = "John Doe")
	private final String nickname;

	@Schema(description = "회원 프로필 이미지 URL", example = "https://example.com/profile-images/random-string/image.jpg")
	private final String profileImageUrl;
}
