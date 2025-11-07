package com.lifelibrarians.lifebookshelf.interview.controller;

import com.lifelibrarians.lifebookshelf.auth.dto.MemberSessionDto;
import com.lifelibrarians.lifebookshelf.auth.jwt.LoginMemberInfo;
import com.lifelibrarians.lifebookshelf.exception.status.AutobiographyExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.annotation.ApiErrorCodeExample;
import com.lifelibrarians.lifebookshelf.interview.dto.request.InterviewConversationCreateRequestDto;
import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewConversationResponseDto;
import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewQuestionResponseDto;
import com.lifelibrarians.lifebookshelf.exception.status.InterviewExceptionStatus;
import com.lifelibrarians.lifebookshelf.interview.service.InterviewFacadeService;
import com.lifelibrarians.lifebookshelf.log.Logging;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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

	@Operation(summary = "챗봇과의 대화 내역 전송 요청", description = "챗봇과의 대화 내역을 전송합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "created"),
	})
	@ApiErrorCodeExample(
			interviewExceptionStatuses = {
					InterviewExceptionStatus.INTERVIEW_NOT_FOUND,
					InterviewExceptionStatus.INTERVIEW_NOT_OWNER,
					InterviewExceptionStatus.INTERVIEW_MAX_CONVERSATIONS_EXCEEDED,
					InterviewExceptionStatus.INTERVIEW_CONTENT_LENGTH_EXCEEDED
			}
	)
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{interviewId}/conversations")
	@ResponseStatus(HttpStatus.CREATED)
	public void sendConversation(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@PathVariable("interviewId") @Parameter(description = "인터뷰 ID", example = "1") Long interviewId,
			@Valid @RequestBody InterviewConversationCreateRequestDto requestDto
	) {
		interviewFacadeService.createConversations(memberSessionDto.getMemberId(), interviewId,
				requestDto);
	}

	@Operation(summary = "현재 진행중인 인터뷰 질문을 다음 질문으로 갱신 요청", description = "현재 진행중인 인터뷰 질문을 다음 질문으로 갱신합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			interviewExceptionStatuses = {
					InterviewExceptionStatus.INTERVIEW_NOT_FOUND,
					InterviewExceptionStatus.INTERVIEW_NOT_OWNER,
					InterviewExceptionStatus.NEXT_INTERVIEW_QUESTION_NOT_FOUND,
			}
	)
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{interviewId}/questions/current-question")
	public void updateCurrentQuestion(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@PathVariable("interviewId") @Parameter(description = "인터뷰 ID", example = "1") Long interviewId
	) {
		interviewFacadeService.updateCurrentQuestion(memberSessionDto.getMemberId(), interviewId);
	}
}
