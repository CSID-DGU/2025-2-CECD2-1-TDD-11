package com.lifelibrarians.lifebookshelf.auth.controller;

import com.lifelibrarians.lifebookshelf.auth.dto.EmailLoginRequestDto;
import com.lifelibrarians.lifebookshelf.auth.dto.JwtLoginTokenDto;
import com.lifelibrarians.lifebookshelf.auth.dto.EmailRegisterRequestDto;
import com.lifelibrarians.lifebookshelf.auth.dto.MemberSessionDto;
import com.lifelibrarians.lifebookshelf.auth.dto.PasswordResetRequestDto;
import com.lifelibrarians.lifebookshelf.auth.dto.VerifyEmailRequestDto;
import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.auth.jwt.LoginMemberInfo;
import com.lifelibrarians.lifebookshelf.auth.password.annotation.OneWayEncryption;
import com.lifelibrarians.lifebookshelf.auth.password.annotation.TargetMapping;
import com.lifelibrarians.lifebookshelf.auth.service.AuthService;
import com.lifelibrarians.lifebookshelf.exception.annotation.ApiErrorCodeExample;
import com.lifelibrarians.lifebookshelf.log.Logging;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
@Tag(name = "인증 (Auth)", description = "인증 관련 API")
@Logging
public class AuthController {

    private final AuthService authService;


    @Operation(summary = "이메일 회원가입", description = "이메일 회원가입을 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "accepted",
                    content = @Content(schema = @Schema(implementation = JwtLoginTokenDto.class))
            ),
    })
    @ApiErrorCodeExample(
            authExceptionStatuses = {
                    AuthExceptionStatus.INVALID_EMAIL_FORMAT,
                    AuthExceptionStatus.EMAIL_TOO_LONG,
                    AuthExceptionStatus.PASSWORD_FORMAT_ERROR,
                    AuthExceptionStatus.MEMBER_ALREADY_EXISTS,
            }
    )
    @PostMapping(value = "/email-register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @OneWayEncryption({
            @TargetMapping(clazz = EmailRegisterRequestDto.class, fields = {
                    EmailRegisterRequestDto.Fields.password})
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void registerEmail(
            @Valid @ModelAttribute EmailRegisterRequestDto requestDto
    ) {
        authService.registerEmail(requestDto);
    }

    @Operation(summary = "이메일 인증 요청", description = "인증코드로 이메일 인증을 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created")
    })
    @ApiErrorCodeExample(
            authExceptionStatuses = {
                    AuthExceptionStatus.INVALID_EMAIL_FORMAT,
                    AuthExceptionStatus.EMAIL_TOO_LONG,
                    AuthExceptionStatus.INVALID_AUTH_CODE
            }
    )
    @PostMapping(value = "/email-verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void verifyEmail(
            @Valid @ModelAttribute VerifyEmailRequestDto requestDto
    ) {
    }

    @Operation(summary = "이메일 로그인", description = "이메일 로그인을 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok")
    })
    @ApiErrorCodeExample(
            authExceptionStatuses = {
                    AuthExceptionStatus.INVALID_EMAIL_FORMAT,
                    AuthExceptionStatus.EMAIL_TOO_LONG,
                    AuthExceptionStatus.PASSWORD_FORMAT_ERROR,
                    AuthExceptionStatus.EMAIL_OR_PASSWORD_INCORRECT,
                    AuthExceptionStatus.EMAIL_NOT_VERIFIED,
                    AuthExceptionStatus.MEMBER_NOT_FOUND
            }
    )
    @PostMapping(value = "/email-login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @OneWayEncryption({
            @TargetMapping(clazz = EmailLoginRequestDto.class, fields = {
                    EmailLoginRequestDto.Fields.password})
    })
    public JwtLoginTokenDto loginEmail(
            @Valid @ModelAttribute EmailLoginRequestDto requestDto
    ) {
        return authService.loginEmail(requestDto);
    }

    @Operation(summary = "비밀번호 초기화 요청", description = "비밀번호 초기화를 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok")
    })
    @ApiErrorCodeExample(
            authExceptionStatuses = {
                    AuthExceptionStatus.INVALID_EMAIL_FORMAT,
                    AuthExceptionStatus.EMAIL_TOO_LONG,
                    AuthExceptionStatus.EMAIL_NOT_VERIFIED,
                    AuthExceptionStatus.MEMBER_NOT_FOUND
            }
    )
    @PostMapping(value = "/reset-password", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void resetPassword(
            @Valid @ModelAttribute PasswordResetRequestDto requestDto
    ) {
    }

    @Operation(summary = "회원탈퇴 요청", description = "회원탈퇴를 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "no content")
    })
    @ApiErrorCodeExample(
            authExceptionStatuses = {
                    AuthExceptionStatus.MEMBER_NOT_FOUND,
                    AuthExceptionStatus.MEMBER_ALREADY_WITHDRAWN
            }
    )
    @DeleteMapping(value = "/unregister")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void unregister(
            @LoginMemberInfo MemberSessionDto memberSessionDto
    ) {
        authService.unregister(memberSessionDto.getMemberId());
    }
}
