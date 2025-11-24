package com.lifelibrarians.lifebookshelf.queue.consumer;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyChapter;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyChapterRepository;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import com.lifelibrarians.lifebookshelf.queue.dto.response.AutobiographyGenerateResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

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

        log.info("자서전 수신: autobiographyId={}, userId={}",
                dto.getAutobiographyId(), dto.getUserId());

        Autobiography autobiography = autobiographyRepository.findById(dto.getAutobiographyId())
                .orElseThrow(() -> new RuntimeException("Autobiography not found: " + dto.getAutobiographyId()));

        Member member = memberRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Member not found: " + dto.getUserId()));

        // autobiography에 대한 chapter 생성
        AutobiographyChapter chapter = AutobiographyChapter.of(
                dto.getTitle(),
                dto.getContent(),
                null,
                now,
                now,
                member,
                autobiography
        );

        autobiographyChapterRepository.save(chapter);
    }
}
