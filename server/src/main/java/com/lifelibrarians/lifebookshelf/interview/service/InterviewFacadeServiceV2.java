package com.lifelibrarians.lifebookshelf.interview.service;

import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewSummaryResponseDto;
import com.lifelibrarians.lifebookshelf.log.Logging;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Logging
public class InterviewFacadeServiceV2 {

	private final InterviewQueryServiceV2 interviewQueryServiceV2;

	public InterviewSummaryResponseDto getInterviewSummaries(Long memberId, Integer year, Integer month) {
		return interviewQueryServiceV2.getInterviewSummaries(memberId, year, month);
	}
}
