package com.lifelibrarians.lifebookshelf.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "재가입 요청 DTO")
@ToString
@FieldNameConstants
public class RejoinRequestDto {

	@Schema(description = "이메일", example = "example@gmail.com")
	private final String email;

	@Schema(description = "비밀번호", example = "securepassword")
	private final String password;

	@Schema(description = "대화 내역 복구 여부 (true: 복구, false: 삭제)", example = "true")
	private final Boolean restoreData;

	@Schema(description = "디바이스 토큰 (Optional)", example = "deviceToken", nullable = true)
	private final String deviceToken;
}
