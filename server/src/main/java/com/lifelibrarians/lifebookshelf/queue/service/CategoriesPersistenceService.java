package com.lifelibrarians.lifebookshelf.queue.service;

import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.classification.repository.ChunkRepository;
import com.lifelibrarians.lifebookshelf.classification.repository.MaterialRepository;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.queue.dto.response.CategoriesPayloadResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Logging
public class CategoriesPersistenceService {
    private final AutobiographyRepository autobiographyRepository;
    private final ChunkRepository chunkRepository;
    private final MaterialRepository materialRepository;

    @Transactional
    public void receiveCategoriesPayload(CategoriesPayloadResponseDto payload) {
        // 사용자 권한 검증
        var autobiography = autobiographyRepository.findById(Long.valueOf(payload.getAutobiographyId()))
            .orElseThrow(() -> new RuntimeException("자서전을 찾을 수 없습니다."));
        
        if (!autobiography.getMember().getId().equals(Long.valueOf(payload.getUserId()))) {
            throw new RuntimeException("해당 자서전에 대한 권한이 없습니다.");
        }

        // Chunk 변화량 처리
        if (payload.getChunks() != null) {
            payload.getChunks().forEach(chunkPayload -> {
                var chunkOpt = chunkRepository.findByAutobiographyAndThemeAndCategoryOrderAndChunkOrder(autobiography.getId(), Long.valueOf(payload.getThemeId()), payload.getCategoryId(), chunkPayload.getChunkOrder());
                if (chunkOpt.isPresent()) {
                    var chunk = chunkOpt.get();
                    int oldWeight = chunk.getWeight();
                    chunk.updateWeight(oldWeight + chunkPayload.getWeight());
                    chunkRepository.save(chunk);
                } else {
                    System.out.println("[SPRING_CHUNK_NOT_FOUND] No chunk found with given parameters");
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
                        System.out.println("[SPRING_MATERIAL_NOT_FOUND] No material found with given parameters");
                    });
            });
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
