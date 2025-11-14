package com.lifelibrarians.lifebookshelf.member.controller;

import com.lifelibrarians.lifebookshelf.auth.dto.MemberSessionDto;
import com.lifelibrarians.lifebookshelf.auth.jwt.LoginMemberInfo;
import com.lifelibrarians.lifebookshelf.exception.annotation.ApiErrorCodeExample;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.dto.request.MemberUpdateV2RequestDto;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberBasicV2ResponseDto;
import com.lifelibrarians.lifebookshelf.exception.status.MemberExceptionStatus;
import com.lifelibrarians.lifebookshelf.member.service.MemberFacadeServiceV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v2/members")
@Tag(name = "회원 V2 (Member V2)", description = "회원 관련 V2 API")
@Logging
public class MemberControllerV2 {

    private final MemberFacadeServiceV2 memberFacadeServiceV2;


    @Operation(summary = "회원 메타데이터 넣기", description = "주제, 연령대, 성별, 직업군, 생성목적 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
    })
    @ApiErrorCodeExample(
            memberExceptionStatuses = {
                    MemberExceptionStatus.MEMBER_NAME_LENGTH_EXCEEDED
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createMember(
            @LoginMemberInfo MemberSessionDto memberSessionDto,
            @Valid @RequestBody MemberUpdateV2RequestDto requestDto
    ) {
        memberFacadeServiceV2.createMember(memberSessionDto.getMemberId(), requestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원 메타데이터 조회", description = "주제, 연령대, 성별, 직업군, 생성목적 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
    })
    @ApiErrorCodeExample(
            memberExceptionStatuses = {
                    MemberExceptionStatus.MEMBER_METADATA_NOT_FOUND
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public MemberBasicV2ResponseDto getMember(
            @LoginMemberInfo MemberSessionDto memberSessionDto
    ) {
        return memberFacadeServiceV2.getMember(memberSessionDto.getMemberId());
    }

}
