package com.lifelibrarians.lifebookshelf.interview.service;

import com.lifelibrarians.lifebookshelf.exception.status.InterviewExceptionStatus;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;

import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewConversationResponseDto;
import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewQuestionResponseDto;
import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewSummaryResponseDto;
import com.lifelibrarians.lifebookshelf.exception.status.CommonExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Logging
public class InterviewFacadeService {

	private final InterviewQueryService interviewQueryService;


	/*-----------------------------------------READ-----------------------------------------*/
	public InterviewConversationResponseDto getConversations(Long memberId, Long interviewId,
			Pageable pageable) {
		Interview interview = interviewQueryService.getInterview(interviewId);
		if (!interview.getMember().getId().equals(memberId)) {
			throw InterviewExceptionStatus.INTERVIEW_NOT_OWNER.toServiceException();
		}
		return interviewQueryService.getConversations(interview, pageable);
	}

	public InterviewQuestionResponseDto getQuestions(Long memberId, Long interviewId) {
		Interview interview = interviewQueryService.getInterview(interviewId);
		if (!interview.getMember().getId().equals(memberId)) {
			throw InterviewExceptionStatus.INTERVIEW_NOT_OWNER.toServiceException();
		}
		return interviewQueryService.getQuestions(interview.getQuestions(),
				interview.getCurrentQuestion());
	}

	public InterviewSummaryResponseDto getInterviewSummaries(Long memberId, Integer year, Integer month) {
		if (year < 2000) {
			throw CommonExceptionStatus.INVALID_YEAR.toServiceException();
		}
		if (month < 1 || month > 12) {
			throw CommonExceptionStatus.INVALID_MONTH.toServiceException();
		}
		return InterviewSummaryResponseDto.builder()
				.interviews(java.util.Collections.emptyList())
				.build();
	}
}
