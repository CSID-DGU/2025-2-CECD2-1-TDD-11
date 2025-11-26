package com.lifelibrarians.lifebookshelf.auth.service;

import com.lifelibrarians.lifebookshelf.auth.domain.TemporaryUser;
import com.lifelibrarians.lifebookshelf.auth.dto.EmailLoginRequestDto;
import com.lifelibrarians.lifebookshelf.auth.dto.EmailRegisterRequestDto;
import com.lifelibrarians.lifebookshelf.auth.dto.JwtLoginTokenDto;
import com.lifelibrarians.lifebookshelf.auth.dto.ReIssueTokenRequestDto;
import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.auth.jwt.JwtTokenProvider;
import lombok.extern.log4j.Log4j2;
import com.lifelibrarians.lifebookshelf.member.domain.LoginType;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.domain.MemberRole;
import com.lifelibrarians.lifebookshelf.member.domain.PasswordMember;
import com.lifelibrarians.lifebookshelf.member.repository.MemberMetadataRepository;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import com.lifelibrarians.lifebookshelf.member.repository.PasswordMemberRepository;

import com.lifelibrarians.lifebookshelf.notification.service.NotificationCommandService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
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
	
	@Qualifier("refreshTokenRedisTemplate")
	private final RedisTemplate<String, String> refreshTokenRedisTemplate;

	public void registerEmail(EmailRegisterRequestDto requestDto) {
		Optional<Member> optionalMember = memberRepository.findByEmail(requestDto.getEmail());
		if (optionalMember.isPresent()) {
			if (optionalMember.get().getDeletedAt() != null) {
				throw AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN.toServiceException();
			} else {
				throw AuthExceptionStatus.MEMBER_ALREADY_EXISTS.toServiceException();
			}
		}

		// String code = generateVerificationCode();
        // emailService.sendVerificationCode(requestDto.getEmail(), code);

        /*
		TemporaryUser temporaryUser = TemporaryUser.builder()
				.email(requestDto.getEmail())
				.password(requestDto.getPassword())
				.code(code)
				.expiresAt(LocalDateTime.now().plusMinutes(5))
				.build();
		temporaryUserStore.save(requestDto.getEmail(), temporaryUser);
         */

        LocalDateTime now = LocalDateTime.now();
        PasswordMember passwordMember = PasswordMember.of(requestDto.getPassword());
        passwordMemberRepository.save(passwordMember);
        Member member = Member.of(
                LoginType.PASSWORD,
                requestDto.getEmail(),
                MemberRole.MEMBER,
                null,
                requestDto.getEmail().split("@")[0] + UUID.randomUUID().toString().substring(0, 6),
                now,
                now,
                null
        );
        member.addPasswordMember(passwordMember);
        memberRepository.save(member);
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

        // 1) 기존 회원 조회
        Optional<Member> optionalMember = memberRepository.findByEmail(requestDto.getEmail());

        Member member = null;

        if (optionalMember.isEmpty()) {
            // ===== 신규 회원 자동 생성 =====
            LocalDateTime now = LocalDateTime.now();
            PasswordMember passwordMember = PasswordMember.of(requestDto.getPassword());
            passwordMemberRepository.save(passwordMember);
            member = Member.of(
                    LoginType.PASSWORD,
                    requestDto.getEmail(),
                    MemberRole.MEMBER,
                    null,
                    requestDto.getEmail().split("@")[0] + UUID.randomUUID().toString().substring(0, 6),
                    now,
                    now,
                    null
            );
            member.addPasswordMember(passwordMember);
            memberRepository.save(member);

        } else {
            // ===== 기존 회원 로그인 =====
            member = optionalMember.get();

            if (member.getDeletedAt() != null) {
                throw AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN.toServiceException();
            }

            if (!member.getPasswordMember().matchPassword(requestDto.getPassword())) {
                throw AuthExceptionStatus.EMAIL_OR_PASSWORD_INCORRECT.toServiceException();
            }

            if (member.getRole() == MemberRole.PRE_MEMBER) {
                throw AuthExceptionStatus.EMAIL_NOT_VERIFIED.toServiceException();
            }
        }

        // 2) 토큰 발급
        Jwt accessToken = jwtTokenProvider.createMemberAccessToken(member.getId());
        Jwt refreshToken = jwtTokenProvider.createMemberRefreshToken(member.getId());

        String memberId = member.getId().toString();

        refreshTokenRedisTemplate.opsForValue()
                .set("refresh:" + memberId, refreshToken.getTokenValue(), Duration.ofDays(30));
        refreshTokenRedisTemplate.opsForValue()
                .set("access:" + memberId, accessToken.getTokenValue(), Duration.ofDays(7));

        // 3) Device Token 업데이트
        if (requestDto.getDeviceToken() != null && !requestDto.getDeviceToken().isEmpty()) {
            notificationService.updateDeviceToken(member, requestDto.getDeviceToken(), LocalDateTime.now());
        }

        // 4) metadata 입력 여부 확인
        boolean metadataSuccessed = memberMetadataRepository.findByMemberId(member.getId())
                .map(meta -> meta.getGender() != null &&
                        meta.getOccupation() != null &&
                        meta.getAgeGroup() != null)
                .orElse(false);

        // 5) 반환
        return JwtLoginTokenDto.builder()
                .accessToken(accessToken.getTokenValue())
                .refreshToken(refreshToken.getTokenValue())
                .metadataSuccessed(metadataSuccessed)
                .build();
    }

    // 토큰 재발급
    public JwtLoginTokenDto reissueToken(ReIssueTokenRequestDto requestDto) {
        // JWT 파싱 + 서명 검증
        Jwt refreshToken = jwtTokenProvider.parseToken(requestDto.getRefreshToken());

        // 만료 확인
        jwtTokenProvider.validateRefreshToken(refreshToken);

        // 토큰에서 memberId 추출
        Long memberId = jwtTokenProvider.extractMemberIdFromRefreshToken(refreshToken);

        // Redis에서 저장된 Refresh Token 확인
        String refreshKey = "refresh:" + memberId;
        String storedRefreshToken = refreshTokenRedisTemplate.opsForValue().get(refreshKey);
        
        if (storedRefreshToken == null || !storedRefreshToken.equals(requestDto.getRefreshToken())) {
            throw AuthExceptionStatus.INVALID_REFRESH_TOKEN.toServiceException();
        }

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);

        if (member.getDeletedAt() != null) {
            throw AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN.toServiceException();
        }

        // 새 토큰 발급 (Rotation)
        Jwt newAccess  = jwtTokenProvider.createMemberAccessToken(memberId);
        Jwt newRefresh = jwtTokenProvider.createMemberRefreshToken(memberId);

        // Redis에 새 토큰들 저장 (기존 토큰 무효화)
        String memberIdStr = memberId.toString();
        refreshTokenRedisTemplate.opsForValue().set("refresh:" + memberIdStr, newRefresh.getTokenValue(), Duration.ofDays(30));
        refreshTokenRedisTemplate.opsForValue().set("access:" + memberIdStr, newAccess.getTokenValue(), Duration.ofDays(7));

        return JwtLoginTokenDto.builder()
                .accessToken(newAccess.getTokenValue())
                .refreshToken(newRefresh.getTokenValue())
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

    public void logout(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);
        if (member.getDeletedAt() != null) {
            throw AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN.toServiceException();
        }
        
        // Redis에서 Access Token과 Refresh Token 모두 삭제
        String memberIdStr = memberId.toString();
        refreshTokenRedisTemplate.delete("access:" + memberIdStr);
        refreshTokenRedisTemplate.delete("refresh:" + memberIdStr);
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
