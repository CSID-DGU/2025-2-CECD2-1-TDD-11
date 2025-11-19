package com.lifelibrarians.lifebookshelf.scheduler.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyStatusRepository;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewRepository;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewService {

    private final AutobiographyStatusRepository autobiographyStatusRepository;
    private final InterviewRepository interviewRepository;
    private final MemberRepository memberRepository;

    @Scheduled(cron = "0 */1 * * * *", zone = "Asia/Seoul") // 매일 자정
    public void createInterviewsForAllMembers() {
        log.info("[InterviewScheduler] Starting daily interview generation...");

        List<Member> allMembers = memberRepository.findAll();

        for (Member member : allMembers) {
            // 멤버의 자서전 상태 조회
            Optional<AutobiographyStatus> optionalStatus =
                    autobiographyStatusRepository.findByMemberId(member.getId());

            if (optionalStatus.isEmpty()) {
                log.warn("[InterviewScheduler] No AutobiographyStatus for member {}", member.getId());
                continue; // 다음 member로 넘어감
            }

            AutobiographyStatus status = optionalStatus.get();

            // creating / finish 상태 제외
            AutobiographyStatusType type = status.getStatus();

            // creating, finish 제외
            if (type == AutobiographyStatusType.CREATING ||
                    type == AutobiographyStatusType.FINISH) {
                continue;
            }

            if (status.getCurrentAutobiography() == null) {
                log.error("[InterviewScheduler] No autobiography found for member {}", member.getId());
                continue;
            }

            Interview interview = Interview.ofV2(
                    LocalDateTime.now(),
                    status.getCurrentAutobiography(),
                    member,
                    null         // summary
            );

            interviewRepository.save(interview);
            log.info("[InterviewScheduler] Interview created for member {}", member.getId());
        }
    }
}