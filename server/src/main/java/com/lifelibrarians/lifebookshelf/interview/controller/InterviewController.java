package com.lifelibrarians.lifebookshelf.interview.controller;

import com.lifelibrarians.lifebookshelf.auth.dto.MemberSessionDto;
import com.lifelibrarians.lifebookshelf.auth.jwt.LoginMemberInfo;
import com.lifelibrarians.lifebookshelf.exception.annotation.ApiErrorCodeExample;
import com.lifelibrarians.lifebookshelf.exception.status.CommonExceptionStatus;
import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewConversationResponseDto;
import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewQuestionResponseDto;

import com.lifelibrarians.lifebookshelf.exception.status.InterviewExceptionStatus;

import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewSummaryOfMonthResponseDto;
import com.lifelibrarians.lifebookshelf.interview.service.InterviewFacadeService;
import com.lifelibrarians.lifebookshelf.log.Logging;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/interviews")
@Tag(name = "인터뷰 (Interview)", description = "인터뷰 관련 API")
@Logging
public class InterviewController {

	private final InterviewFacadeService interviewFacadeService;

	@Operation(summary = "인터뷰 대화 조회", description = "인터뷰 대화 내역을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			interviewExceptionStatuses = {
					InterviewExceptionStatus.INTERVIEW_NOT_FOUND,
					InterviewExceptionStatus.INTERVIEW_NOT_OWNER,
			}
	)
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{interviewId}/conversations")
	public InterviewConversationResponseDto getInterviewConversations(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@PathVariable("interviewId") @Parameter(description = "인터뷰 ID", example = "1") Long interviewId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size
	) {
		return interviewFacadeService.getConversations(memberSessionDto.getMemberId(), interviewId,
				PageRequest.of(page, size));
	}

	@Operation(summary = "인터뷰 질문 목록 조회", description = "인터뷰 질문 목록을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			interviewExceptionStatuses = {
					InterviewExceptionStatus.INTERVIEW_NOT_FOUND,
					InterviewExceptionStatus.INTERVIEW_NOT_OWNER,
			}
	)
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{interviewId}/questions")
	public InterviewQuestionResponseDto getInterviewQuestions(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@PathVariable("interviewId") @Parameter(description = "인터뷰 ID", example = "1") Long interviewId
	) {
		return interviewFacadeService.getQuestions(memberSessionDto.getMemberId(), interviewId);
	}

    @Operation(summary = "특정 날짜의 인터뷰 요약 조회", description = "달-일 별로 summary한 conversations(사용자응답)의 갯수와 요약본을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @ApiErrorCodeExample(
            commonExceptionStatuses = {
                    CommonExceptionStatus.INVALID_YEAR,
                    CommonExceptionStatus.INVALID_MONTH,
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{autobiographyId}/interviews/summaries")
    public InterviewSummaryOfMonthResponseDto getInterviewSummaries(
            @LoginMemberInfo MemberSessionDto memberSessionDto,
            @PathVariable ("autobiographyId") @Parameter(description = "자서전 ID", example = "1") Long autobiographyId,
            @RequestParam("year") @Parameter(description = "년도", example = "2024") Integer year,
            @RequestParam("month") @Parameter(description = "월", example = "12") Integer month
    ) {
        return interviewFacadeService.getInterviewSummaries(memberSessionDto.getMemberId(), autobiographyId, year, month);
    }
}
