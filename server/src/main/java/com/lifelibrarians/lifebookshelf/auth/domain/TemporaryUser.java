package com.lifelibrarians.lifebookshelf.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class TemporaryUser {
	private String email;
	private String password;
	private String code;
	private LocalDateTime expiresAt;

	public boolean isExpired() {
		return LocalDateTime.now().isAfter(expiresAt);
	}

	public boolean matchCode(String inputCode) {
		return code.equals(inputCode);
	}
}
