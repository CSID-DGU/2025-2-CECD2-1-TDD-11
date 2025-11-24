package com.lifelibrarians.lifebookshelf.queue.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.interview.domain.ConversationType;
import com.lifelibrarians.lifebookshelf.interview.repository.ConversationRepository;
import com.lifelibrarians.lifebookshelf.queue.dto.request.CycleInitRequestDto;
import com.lifelibrarians.lifebookshelf.queue.publisher.CycleInitPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CycleInitService {
    private final AutobiographyRepository autobiographyRepository;
    private final ConversationRepository conversationRepository;
    private final CycleInitPublisher cycleInitPublisher;

    @Transactional
    public String initializeCycleProcess(Autobiography autobiography) {
        String cycleId = UUID.randomUUID().toString();
        
        // AutobiographyCompletionService와 동일한 로직으로 expected count 계산
        int expectedCount = calculateExpectedCount(autobiography);
        
        CycleInitRequestDto cycleInitRequest = CycleInitRequestDto.builder()
                .cycleId(cycleId)
                .expectedCount(expectedCount)
                .autobiographyId(autobiography.getId())
                .userId(autobiography.getMember().getId())
                .build();
                
        cycleInitPublisher.publishAutobiographyCycleInitRequest(cycleInitRequest);
        
        log.info("Cycle initialized - cycleId: {}, expectedCount: {}, autobiographyId: {}", 
                cycleId, expectedCount, autobiography.getId());
                
        return cycleId;
    }
    
    private int calculateExpectedCount(Autobiography autobiography) {
        // HUMAN 타입 conversations에서 카테고리별로 그룹핑하여 개수 계산
        return (int) conversationRepository
                .findByAutobiographyId(autobiography.getId())
                .stream()
                .filter(conv -> ConversationType.HUMAN.equals(conv.getConversationType()))
                .filter(conv -> conv.getMaterials() != null && !conv.getMaterials().isEmpty())
                .collect(Collectors.groupingBy(conv -> extractCategoryOrder(conv.getMaterials())))
                .size();
    }
    
    private Integer extractCategoryOrder(String materials) {
        try {
            String cleaned = materials.trim().replaceAll("\\s+", "");
            if (cleaned.startsWith("[[")) {
                int firstComma = cleaned.indexOf(',');
                if (firstComma > 2) {
                    return Integer.parseInt(cleaned.substring(2, firstComma));
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract category order from materials: {}", materials);
        }
        return 0;
    }
}
