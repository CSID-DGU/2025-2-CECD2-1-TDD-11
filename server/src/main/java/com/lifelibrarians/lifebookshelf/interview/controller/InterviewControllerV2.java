package com.lifelibrarians.lifebookshelf.interview.controller;

import com.lifelibrarians.lifebookshelf.auth.dto.MemberSessionDto;
import com.lifelibrarians.lifebookshelf.auth.jwt.LoginMemberInfo;
import com.lifelibrarians.lifebookshelf.exception.annotation.ApiErrorCodeExample;
import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewSummaryResponseDto;
import com.lifelibrarians.lifebookshelf.exception.status.CommonExceptionStatus;
import com.lifelibrarians.lifebookshelf.interview.service.InterviewFacadeService;
import com.lifelibrarians.lifebookshelf.log.Logging;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v2/interviews")
@Tag(name = "인터뷰 V2 (Interview V2)", description = "인터뷰 관련 API V2")
@Logging
public class InterviewControllerV2 {

	private final InterviewFacadeService interviewFacadeService;

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
	@GetMapping("/summaries")
	public InterviewSummaryResponseDto getInterviewSummaries(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@RequestParam("year") @Parameter(description = "년도", example = "2024") Integer year,
			@RequestParam("month") @Parameter(description = "월", example = "12") Integer month
	) {
		return interviewFacadeService.getInterviewSummaries(memberSessionDto.getMemberId(), year, month);
	}
}