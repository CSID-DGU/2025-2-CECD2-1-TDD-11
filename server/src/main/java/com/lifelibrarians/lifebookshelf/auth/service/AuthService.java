package com.lifelibrarians.lifebookshelf.auth.service;

import com.lifelibrarians.lifebookshelf.auth.domain.TemporaryUser;
import com.lifelibrarians.lifebookshelf.auth.dto.EmailLoginRequestDto;
import com.lifelibrarians.lifebookshelf.auth.dto.EmailRegisterRequestDto;
import com.lifelibrarians.lifebookshelf.auth.dto.JwtLoginTokenDto;
import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.auth.jwt.JwtTokenProvider;
import com.lifelibrarians.lifebookshelf.log.Logging;
import lombok.extern.log4j.Log4j2;
import com.lifelibrarians.lifebookshelf.member.domain.LoginType;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.domain.MemberRole;
import com.lifelibrarians.lifebookshelf.member.domain.PasswordMember;
import com.lifelibrarians.lifebookshelf.member.repository.MemberMetadataRepository;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import com.lifelibrarians.lifebookshelf.member.repository.PasswordMemberRepository;

import com.lifelibrarians.lifebookshelf.notification.service.NotificationCommandService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final MemberRepository memberRepository;
	private final PasswordMemberRepository passwordMemberRepository;
	private final MemberMetadataRepository memberMetadataRepository;
	private final NotificationCommandService notificationService;
	private final EmailService emailService;
	private final TemporaryUserStore temporaryUserStore;

	public void registerEmail(EmailRegisterRequestDto requestDto) {
		Optional<Member> optionalMember = memberRepository.findByEmail(requestDto.getEmail());
		if (optionalMember.isPresent()) {
			if (optionalMember.get().getDeletedAt() != null) {
				throw AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN.toServiceException();
			} else {
				throw AuthExceptionStatus.MEMBER_ALREADY_EXISTS.toServiceException();
			}
		}

		String code = generateVerificationCode();
        emailService.sendVerificationCode(requestDto.getEmail(), code);

		TemporaryUser temporaryUser = TemporaryUser.builder()
				.email(requestDto.getEmail())
				.password(requestDto.getPassword())
				.code(code)
				.expiresAt(LocalDateTime.now().plusMinutes(5))
				.build();
		temporaryUserStore.save(requestDto.getEmail(), temporaryUser);
	}

	public void verifyEmail(String email, String code) {
        log.info("[VERIFY_EMAIL] 이메일 인증 시도 - email: {}, code: {}", email, code);

        Optional<TemporaryUser> optionalUser = temporaryUserStore.find(email);
        if (optionalUser.isEmpty()) {
            log.warn("[VERIFY_EMAIL] 인증 실패 - 해당 이메일에 대한 임시 사용자 없음 (email: {})", email);
            throw AuthExceptionStatus.NOT_FOUND_EMAIL.toServiceException();
        }

        TemporaryUser temporaryUser = optionalUser.get();
        log.info("[VERIFY_EMAIL] 임시 사용자 로드 성공 - user: {}", temporaryUser);

        if (!temporaryUser.matchCode(code)) {
            log.warn("[VERIFY_EMAIL] 인증 코드 불일치 - 입력: {}, 기대값: {}", code, temporaryUser.getCode());
            throw AuthExceptionStatus.INVALID_AUTH_CODE.toServiceException();
        }

        log.info("[VERIFY_EMAIL] 인증 코드 일치 - 임시 계정으로 회원 생성 진행");

        LocalDateTime now = LocalDateTime.now();
		PasswordMember passwordMember = PasswordMember.of(temporaryUser.getPassword());
		passwordMemberRepository.save(passwordMember);
		Member member = Member.of(
				LoginType.PASSWORD,
				temporaryUser.getEmail(),
				MemberRole.MEMBER,
				null,
				temporaryUser.getEmail().split("@")[0] + UUID.randomUUID().toString().substring(0, 6),
				now,
				now,
				null
		);
		member.addPasswordMember(passwordMember);
		memberRepository.save(member);
		temporaryUserStore.remove(email);
	}

	public JwtLoginTokenDto loginEmail(EmailLoginRequestDto requestDto) {
		Optional<Member> member = memberRepository.findByEmail(requestDto.getEmail());

		if (member.isPresent() && member.get().getDeletedAt() != null) {
			throw AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN.toServiceException();
		}

		if (member.isEmpty() || !member.get().getPasswordMember()
				.matchPassword(requestDto.getPassword())) {
			throw AuthExceptionStatus.EMAIL_OR_PASSWORD_INCORRECT.toServiceException();
		}

		if (member.get().getRole() == MemberRole.PRE_MEMBER) {
			throw AuthExceptionStatus.EMAIL_NOT_VERIFIED.toServiceException();
		}

		if (member.get().getDeletedAt() != null) {
			throw AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN.toServiceException();
		}

		Jwt accessToken = jwtTokenProvider.createMemberAccessToken(member.get().getId());
		Jwt refreshToken = jwtTokenProvider.createMemberRefreshToken(member.get().getId());

		if (requestDto.getDeviceToken() != null && !requestDto.getDeviceToken().isEmpty()) {
			notificationService.updateDeviceToken(member.get(), requestDto.getDeviceToken(),
					LocalDateTime.now());
		}

		boolean metadataSuccessed = memberMetadataRepository.findByMemberId(member.get().getId())
				.map(metadata -> metadata.getGender() != null 
						&& metadata.getOccupation() != null 
						&& metadata.getAgeGroup() != null)
				.orElse(false);

		return JwtLoginTokenDto.builder()
				.accessToken(accessToken.getTokenValue())
				.refreshToken(refreshToken.getTokenValue())
				.metadataSuccessed(metadataSuccessed)
				.build();
	}

	public void resetPassword(String email) {
		Member member = memberRepository.findByEmail(email)
				.orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);

		if (member.getDeletedAt() != null) {
			throw AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN.toServiceException();
		}

		String tempPassword = generateTemporaryPassword();
		member.getPasswordMember().updatePassword(tempPassword);
		passwordMemberRepository.save(member.getPasswordMember());
		emailService.sendTemporaryPassword(email, tempPassword);
	}

	public void unregister(Long memberId) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);
		LocalDateTime now = LocalDateTime.now();
		member.softDelete(now);
		memberRepository.save(member);
	}

	private String generateVerificationCode() {
		return String.format("%06d", new Random().nextInt(1000000));
	}

	private String generateTemporaryPassword() {
		return UUID.randomUUID().toString().substring(0, 12);
	}
}
