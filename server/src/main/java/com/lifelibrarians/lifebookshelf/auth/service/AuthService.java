package com.lifelibrarians.lifebookshelf.auth.service;

import com.lifelibrarians.lifebookshelf.auth.dto.EmailLoginRequestDto;
import com.lifelibrarians.lifebookshelf.auth.dto.EmailRegisterRequestDto;
import com.lifelibrarians.lifebookshelf.auth.dto.JwtLoginTokenDto;
import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.auth.jwt.JwtTokenProvider;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.domain.LoginType;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.domain.MemberRole;
import com.lifelibrarians.lifebookshelf.member.domain.PasswordMember;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import com.lifelibrarians.lifebookshelf.member.repository.PasswordMemberRepository;

import com.lifelibrarians.lifebookshelf.notification.service.NotificationCommandService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Logging
@Transactional
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final MemberRepository memberRepository;
	private final PasswordMemberRepository passwordMemberRepository;
	private final NotificationCommandService notificationService;

	public void registerEmail(EmailRegisterRequestDto requestDto) {
//		1. 이메일 중복 확인
		Optional<Member> optionalMember = memberRepository.findByEmail(requestDto.getEmail());
		if (optionalMember.isPresent()) {
			if (optionalMember.get().getDeletedAt() != null) {
				// 1-1. 이미 탈퇴한 회원인 경우
				throw AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN.toServiceException();
			} else {
				throw AuthExceptionStatus.MEMBER_ALREADY_EXISTS.toServiceException();
			}
		}

//		2. 회원가입
		LocalDateTime now = LocalDateTime.now();
		PasswordMember passwordMember = PasswordMember.of(
				requestDto.getPassword()
		);
		passwordMemberRepository.save(passwordMember);
		Member member = Member.of(
				LoginType.PASSWORD,
				requestDto.getEmail(),
				MemberRole.MEMBER,
				null,
//              이메일 @ 앞 부분 + 6자리 랜덤 문자열
				requestDto.getEmail().split("@")[0] + UUID.randomUUID().toString().substring(0, 6),
				now,
				now,
				null
		);
		member.addPasswordMember(passwordMember);
		memberRepository.save(member);
	}

	public JwtLoginTokenDto loginEmail(EmailLoginRequestDto requestDto) {
//		1. 이메일로 회원 조회
		Optional<Member> member = memberRepository.findByEmail(requestDto.getEmail());

//		1-1. 이미 탈퇴한 회원인 경우
		if (member.isPresent() && member.get().getDeletedAt() != null) {
			throw AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN.toServiceException();
		}

//		2. 이메일 혹은 비밀번호가 틀린 경우
		if (member.isEmpty() || !member.get().getPasswordMember()
				.matchPassword(requestDto.getPassword())) {
			throw AuthExceptionStatus.EMAIL_OR_PASSWORD_INCORRECT.toServiceException();
		}

//      3. 이메일 인증이 되지 않은 회원인 경우
		if (member.get().getRole() == MemberRole.PRE_MEMBER) {
			throw AuthExceptionStatus.EMAIL_NOT_VERIFIED.toServiceException();
		}

//	    4. 이미 탈퇴한 회원인 경우
		if (member.get().getDeletedAt() != null) {
			throw AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN.toServiceException();
		}

//		5. JWT 토큰 생성
		Jwt jwt = jwtTokenProvider.createMemberAccessToken(member.get().getId());

//		6. 디바이스 토큰 추가
		if (requestDto.getDeviceToken() != null && !requestDto.getDeviceToken().isEmpty()) {
			notificationService.updateDeviceToken(member.get(), requestDto.getDeviceToken(),
					LocalDateTime.now());
		}
		return JwtLoginTokenDto.builder()
				.accessToken(jwt.getTokenValue())
				.build();
	}

	public void unregister(Long memberId) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);
		LocalDateTime now = LocalDateTime.now();
		member.softDelete(now);
		memberRepository.save(member);
	}
}
