package com.lifelibrarians.lifebookshelf.queue.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.classification.domain.Chunk;
import com.lifelibrarians.lifebookshelf.classification.repository.ChunkRepository;
import com.lifelibrarians.lifebookshelf.classification.repository.MaterialRepository;
import com.lifelibrarians.lifebookshelf.queue.dto.response.CategoriesPayloadResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriesPersistenceService {
    private final AutobiographyRepository autobiographyRepository;
    private final ChunkRepository chunkRepository;
    private final MaterialRepository materialRepository;

    private final AutobiographyCompletionService autobiographyCompletionService;

    @Transactional
    public void receiveCategoriesPayload(CategoriesPayloadResponseDto payload) {
        // 사용자 권한 검증
        Autobiography autobiography = autobiographyRepository.findById(payload.getAutobiographyId())
            .orElseThrow(() -> new RuntimeException("자서전을 찾을 수 없습니다."));
        
        if (!autobiography.getMember().getId().equals(payload.getUserId())) {
            throw new RuntimeException("해당 자서전에 대한 권한이 없습니다.");
        }

        // Chunk 변화량 처리
        if (payload.getChunks() != null) {
            payload.getChunks().forEach(chunkPayload -> {
                var chunkOpt = chunkRepository.findByAutobiographyAndThemeAndCategoryOrderAndChunkOrder(autobiography.getId(), (long) payload.getThemeId(), payload.getCategoryId(), chunkPayload.getChunkOrder());
                if (chunkOpt.isPresent()) {
                    Chunk chunk = chunkOpt.get();
                    int oldWeight = chunk.getWeight();
                    chunk.updateWeight(oldWeight + chunkPayload.getWeight());
                    chunkRepository.save(chunk);
                } else {
                    log.info("[SPRING_CHUNK_NOT_FOUND] No chunk found with parameters");
                }
            });
        }

        // Material 변화량 처리
        if (payload.getMaterials() != null) {
            payload.getMaterials().forEach(materialPayload -> {
                materialRepository.findByAutobiographyAndThemeAndOrdersAndMaterialOrder(autobiography.getId(), Long.valueOf(payload.getThemeId()), payload.getCategoryId(), materialPayload.getChunkId(), materialPayload.getMaterialOrder())
                    .filter(material -> material.getChunk().getCategory().getAutobiography().getId().equals(autobiography.getId()))
                    .ifPresentOrElse(material -> {
                        // 변화량 적용
                        material.updateExample(material.getExample() + materialPayload.getExample());
                        material.updateSimilarEvent(material.getSimilarEvent() + materialPayload.getSimilarEvent());
                        material.updateCount(material.getCount() + materialPayload.getCount());
                        
                        // Principle 배열 업데이트
                        int[] currentPrinciple = parsePrincipleArray(material.getPrinciple());
                        int[] deltaPrinciple = materialPayload.getPrinciple().stream().mapToInt(Integer::intValue).toArray();
                        for (int i = 0; i < Math.min(currentPrinciple.length, deltaPrinciple.length); i++) {
                            currentPrinciple[i] += deltaPrinciple[i];
                        }
                        material.updatePrinciple(Arrays.toString(currentPrinciple));
                        
                        materialRepository.save(material);
                    }, () -> {
                        log.info("[SPRING_MATERIAL_NOT_FOUND] No material found with parameters");
                    });
            });
        }

        String coShowExampleName = "사용자"; // 기본 이름 설정

        // 자서전 완료 여부 체크 및 상태 업데이트
        boolean isEnough = autobiographyCompletionService.checkCompletionAndTriggerPublication(autobiography, coShowExampleName);
        if (isEnough) {
            LocalDateTime now = LocalDateTime.now();
            AutobiographyStatus status = autobiography.getAutobiographyStatus();
            status.updateStatusType(AutobiographyStatusType.ENOUGH, now); // 상태 업데이트
            log.info("[SPRING_AUTOBIOGRAPHY_ENOUGH] Autobiography {} marked as ENOUGH", autobiography.getId());
        }
    }

    private int[] parsePrincipleArray(String principleStr) {
        try {
            return Arrays.stream(principleStr.replace("[", "").replace("]", "").split(","))
                    .mapToInt(s -> Integer.parseInt(s.trim()))
                    .toArray();
        } catch (Exception e) {
            return new int[]{0,0,0,0,0,0}; // 기본값
        }
    }
}
