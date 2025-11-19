package com.lifelibrarians.lifebookshelf.classification.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.classification.domain.*;
import com.lifelibrarians.lifebookshelf.classification.repository.CategoryRepository;
import com.lifelibrarians.lifebookshelf.classification.repository.ChunkRepository;
import com.lifelibrarians.lifebookshelf.classification.repository.MaterialRepository;
import com.lifelibrarians.lifebookshelf.classification.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@Transactional
@RequiredArgsConstructor
public class ClassificationInitService {

    private final ThemeRepository themeRepository;
    private final CategoryRepository categoryRepository;
    private final ChunkRepository chunkRepository;
    private final MaterialRepository materialRepository;

    public void initializeFromAiData(Autobiography autobiography) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode themeData = mapper.readTree(new ClassPathResource("data/theme.json").getInputStream());
            JsonNode materialData = mapper.readTree(new ClassPathResource("data/material.json").getInputStream());

            // Theme & Category 생성
            JsonNode themes = themeData.get("theme");
            for (int i = 0; i < themes.size(); i++) {
                JsonNode themeNode = themes.get(i);
                String themeName = themeNode.get("name").asText();

                Theme theme = Theme.of(i + 1, ThemeNameType.fromKoreanName(themeName));
                themeRepository.save(theme);

                JsonNode categories = themeNode.get("category");
                for (int j = 0; j < categories.size(); j++) {
                    String categoryName = categories.get(j).asText();

                    Category category = Category.of(j + 1, categoryName, theme, autobiography);
                    categoryRepository.save(category);

                    // 해당 카테고리의 Chunk & Material 더미 생성
                    createChunksAndMaterials(materialData, category, categoryName);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("AI 데이터 초기화 실패", e);
        }
    }

    private void createChunksAndMaterials(JsonNode materialData, Category category, String categoryName) {
        JsonNode categories = materialData.get("category");

        for (JsonNode categoryNode : categories) {
            if (categoryNode.get("name").asText().equals(categoryName)) {
                JsonNode chunks = categoryNode.get("chunk");

                for (int i = 0; i < chunks.size(); i++) {
                    JsonNode chunkNode = chunks.get(i);
                    String chunkName = chunkNode.get("name").asText();

                    Chunk chunk = Chunk.of(i + 1, chunkName, 0, category); // weight 초기값 0
                    chunkRepository.save(chunk);

                    // Material 생성
                    JsonNode materials = chunkNode.get("material");
                    for (int j = 0; j < materials.size(); j++) {
                        String materialName = materials.get(j).asText();

                        Material material = Material.of(
                                j + 1, materialName, 0, 0, 0,
                                Arrays.toString(new int[]{0,0,0,0,0,0}), chunk
                        );
                        materialRepository.save(material);
                    }
                }
                break;
            }
        }
    }
}