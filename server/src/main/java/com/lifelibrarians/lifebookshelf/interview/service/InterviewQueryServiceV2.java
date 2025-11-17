package com.lifelibrarians.lifebookshelf.interview.service;

import com.lifelibrarians.lifebookshelf.exception.status.CommonExceptionStatus;
import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewSummaryResponseDto;
import com.lifelibrarians.lifebookshelf.log.Logging;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Logging
public class InterviewQueryServiceV2 {

	public InterviewSummaryResponseDto getInterviewSummaries(Long memberId, Integer year, Integer month) {
		if (year < 2000) {
			throw CommonExceptionStatus.YEAR_OUT_OF_BOUNDS.toServiceException();
		}
		if (month < 1 || month > 12) {
			throw CommonExceptionStatus.MONTH_OUT_OF_BOUNDS.toServiceException();
		}
		
		// TODO: 실제 구현 필요 - Interview summary. 이거 아직도 안했엇네
		// 현재는 빈 리스트 반환
		List<InterviewSummaryResponseDto.InterviewSummaryDto> summaries = new ArrayList<>();
		
		return InterviewSummaryResponseDto.builder()
				.interviews(summaries)
				.build();
	}
}
