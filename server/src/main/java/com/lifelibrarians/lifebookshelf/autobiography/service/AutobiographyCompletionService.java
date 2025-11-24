package com.lifelibrarians.lifebookshelf.autobiography.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import com.lifelibrarians.lifebookshelf.classification.repository.CategoryRepository;
import com.lifelibrarians.lifebookshelf.classification.repository.MaterialRepository;
import com.lifelibrarians.lifebookshelf.interview.domain.Conversation;
import com.lifelibrarians.lifebookshelf.interview.domain.ConversationType;
import com.lifelibrarians.lifebookshelf.interview.repository.ConversationRepository;
import com.lifelibrarians.lifebookshelf.queue.dto.request.AutobiographyGenerateRequestDto;
import com.lifelibrarians.lifebookshelf.queue.publisher.AutobiographyGeneratePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutobiographyCompletionService {

    private final MaterialRepository materialRepository;
    private final CategoryRepository categoryRepository;
    private final ConversationRepository conversationRepository;
    private final AutobiographyGeneratePublisher autobiographyGeneratePublisher;
    
    private static final double COMPLETION_THRESHOLD = 0.7; // 70%
    private final Map<Long, Boolean> processedAutobiographies = new ConcurrentHashMap<>();

    @Transactional(readOnly = true)
    public boolean checkCompletionAndTriggerPublication(Autobiography autobiography) {
        // 이미 처리된 자서전은 스킵
        if (processedAutobiographies.containsKey(autobiography.getId())) {
            return false;
        }

        // 빠른 완료율 체크만 먼저
        double completionRate = calculateCompletionRateQuick(autobiography);
        
        if (completionRate < COMPLETION_THRESHOLD) {
            return false; // 70% 미만이면 바로 리턴
        }

        log.info("Autobiography {} reached completion threshold: {}", autobiography.getId(), completionRate);
        
        // 70% 이상일 때만 복잡한 로직 실행
        triggerPublicationRequest(autobiography);
        processedAutobiographies.put(autobiography.getId(), true);
        
        return true;
    }

    @Cacheable(value = "completionRate", key = "#autobiography.id + '_' + #autobiography.member.id")
    private double calculateCompletionRateQuick(Autobiography autobiography) {
        Long totalMaterials = materialRepository.countMaterialsByAutobiographyTheme(
            autobiography.getMember().getId(), 
            autobiography.getTheme()
        );
        
        Long completedMaterials = materialRepository.countCompletedMaterialsByAutobiographyTheme(
            autobiography.getMember().getId(), 
            autobiography.getTheme()
        );
        
        if (totalMaterials == 0) return 0.0;
        
        return (double) completedMaterials / totalMaterials;
    }

    public void triggerPublicationRequest(Autobiography autobiography) {
        
        LocalDateTime now = LocalDateTime.now();

        List<AutobiographyGenerateRequestDto> requests = createGenerateRequests(autobiography);

        AutobiographyStatus status = autobiography.getAutobiographyStatus();
        status.updateStatusType(AutobiographyStatusType.CREATING, now); // 상태 업데이트

        // category order 순으로 순차 발행
        requests.stream()
                .sorted((a, b) -> a.getAutobiographyInfo().getCategory().compareTo(b.getAutobiographyInfo().getCategory()))
                .forEach(autobiographyGeneratePublisher::publishGenerateAutobiographyRequest);
        
        log.info("[DEBUG] triggerPublicationRequest 완료 - autobiographyId: {}", autobiography.getId());
    }

    private List<AutobiographyGenerateRequestDto> createGenerateRequests(Autobiography autobiography) {
        log.info("[DEBUG] createGenerateRequests 시작 - autobiographyId: {}", autobiography.getId());
        
        // HUMAN 타입 conversations에서 materials의 첫 번째 숫자로 category order 추출
        Map<Integer, List<String>> conversationsByCategory = conversationRepository
                .findByAutobiographyId(autobiography.getId())
                .stream()
                .filter(conv -> ConversationType.HUMAN.equals(conv.getConversationType()))
                .filter(conv -> conv.getMaterials() != null && !conv.getMaterials().isEmpty())
                .collect(Collectors.groupingBy(
                    conv -> extractCategoryOrder(conv.getMaterials()),
                    Collectors.mapping(Conversation::getContent, Collectors.toList())
                ));

        List<AutobiographyGenerateRequestDto> requests = conversationsByCategory.entrySet().stream()
                .map(entry -> createRequestDto(autobiography, entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        
        log.info("[DEBUG] createGenerateRequests 완료 - 생성된 요청: {}개", requests.size());
        return requests;
    }

    private AutobiographyGenerateRequestDto createRequestDto(Autobiography autobiography, Integer categoryOrder, List<String> conversations) {
        String categoryName = getCategoryName(categoryOrder);
        
        return AutobiographyGenerateRequestDto.builder()
                .autobiographyId(autobiography.getId())
                .userId(autobiography.getMember().getId())
                .userInfo(AutobiographyGenerateRequestDto.UserInfo.builder()
                        .gender(autobiography.getMember().getMemberMemberMetadata().getGender().name())
                        .occupation(autobiography.getMember().getMemberMemberMetadata().getOccupation())
                        .ageGroup(autobiography.getMember().getMemberMemberMetadata().getAgeGroup())
                        .build())
                .autobiographyInfo(AutobiographyGenerateRequestDto.AutobiographyInfo.builder()
                        .theme(autobiography.getTheme())
                        .reason(autobiography.getReason())
                        .category(categoryName)
                        .build())
                .answers(conversations.stream()
                        .map(content -> AutobiographyGenerateRequestDto.InterviewAnswer.builder()
                                .content(content)
                                .conversationType("HUMAN")
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private Integer extractCategoryOrder(String materials) {
        log.info("[DEBUG] extractCategoryOrder - 원본 materials: '{}'", materials);
        // materials에서 첫 번째 배열의 첫 번째 숫자 추출 (예: "[[1, 1, 2], [1, 1, 5]]" -> 1)
        try {
            String cleaned = materials.trim().replaceAll("\\s+", "");
            
            if (cleaned.startsWith("[[")) {
                int firstComma = cleaned.indexOf(',');
                
                if (firstComma > 2) {
                    String firstNumber = cleaned.substring(2, firstComma);
                    log.info("[DEBUG] extractCategoryOrder - 추출된 첫 번째 숫자: '{}'", firstNumber);
                    return Integer.parseInt(firstNumber);
                } else {
                    log.warn("[DEBUG] extractCategoryOrder - 첫 번째 쉼표 위치가 2 이하: {}", firstComma);
                }
            } else {
                log.warn("[DEBUG] extractCategoryOrder - '[[' 로 시작하지 않음");
            }
        } catch (Exception e) {
            log.warn("[DEBUG] extractCategoryOrder 예외 발생 - materials: '{}', error: {}", materials, e.getMessage());
        }
        log.warn("[DEBUG] extractCategoryOrder 기본값 0 반환 - materials: '{}'", materials);
        return 0;
    }

    private String getCategoryName(Integer categoryOrder) {
        return categoryRepository.findNameByOrder(categoryOrder)
                .orElse("default");
    }
}
