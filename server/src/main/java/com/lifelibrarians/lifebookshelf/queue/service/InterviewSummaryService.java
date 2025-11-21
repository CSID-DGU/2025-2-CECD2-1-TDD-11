package com.lifelibrarians.lifebookshelf.queue.service;

import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewRepository;
import com.lifelibrarians.lifebookshelf.queue.dto.response.InterviewSummaryResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewSummaryService {
    private final InterviewRepository interviewRepository;

    @Transactional
    public void saveInterviewSummary(InterviewSummaryResponseDto payload) {
        // Implementation to save the interview summary
        Optional<Interview> optionalInterview = interviewRepository.findById(payload.getInterviewId());

        if (optionalInterview.isEmpty()) {
            log.warn("[InterviewSummaryService] Interview not found with id: {}", payload.getInterviewId());
            return;
        }

        Interview interview = optionalInterview.get();

        interview.updateSummary(payload.getSummary());

        interviewRepository.save(interview);
    }
}
