package com.lifelibrarians.lifebookshelf.queue.consumer;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyChapter;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyChapterRepository;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import com.lifelibrarians.lifebookshelf.queue.dto.response.AutobiographyGenerateResponseDto;
import com.lifelibrarians.lifebookshelf.queue.dto.response.AutobiographyMergeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutobiographyGenerationConsumer {

    private final AutobiographyRepository autobiographyRepository;
    private final MemberRepository memberRepository;
    private final AutobiographyChapterRepository autobiographyChapterRepository;

    @RabbitListener(queues = "autobiography.trigger.result.queue")
    public void receive(AutobiographyGenerateResponseDto dto) {
        LocalDateTime now = LocalDateTime.now();

        // cycleId가 없으면 메시지 거부 (새로운 사이클 관리 시스템 필수)
        if (dto.getCycleId() == null || dto.getCycleId().isEmpty()) {
            log.warn("Message rejected: cycleId is required for new cycle management system - autobiographyId: {}", dto.getAutobiographyId());
            return;
        }

        log.info("자서전 수신: autobiographyId={}, userId={}, cycleId={}, step={}",
                dto.getAutobiographyId(), dto.getUserId(), dto.getCycleId(), dto.getStep());

        Autobiography autobiography = autobiographyRepository.findById(dto.getAutobiographyId())
                .orElseThrow(() -> new RuntimeException("Autobiography not found: " + dto.getAutobiographyId()));

        Member member = memberRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Member not found: " + dto.getUserId()));

        // autobiography에 대한 chapter 생성
        AutobiographyChapter chapter = AutobiographyChapter.of(
                dto.getTitle(),
                dto.getContent(),
                null, // step을 chapterOrder로 사용
                now,
                now,
                member,
                autobiography
        );

        autobiographyChapterRepository.save(chapter);
    }

    @RabbitListener(queues = "autobiography.trigger.cycle.merge.queue")
    @Transactional
    public void handleCycleCompletion(AutobiographyMergeResponseDto dto) {
        LocalDateTime now = LocalDateTime.now();
        
        log.info("사이클 완료 수신: cycleId={}, autobiographyId={}, userId={}, action={}", 
                dto.getCycleId(), dto.getAutobiographyId(), dto.getUserId(), dto.getAction());

        Autobiography autobiography = autobiographyRepository.findById(dto.getAutobiographyId())
                .orElseThrow(() -> new RuntimeException("Autobiography not found: " + dto.getAutobiographyId()));

        // 자서전 상태를 COMPLETED로 변경
        AutobiographyStatus status = autobiography.getAutobiographyStatus();
        status.updateStatusType(AutobiographyStatusType.FINISH, now);
        
        autobiographyRepository.save(autobiography);
        
        log.info("자서전 생성 완료: autobiographyId={}, cycleId={}", 
                autobiography.getId(), dto.getCycleId());
    }
}
