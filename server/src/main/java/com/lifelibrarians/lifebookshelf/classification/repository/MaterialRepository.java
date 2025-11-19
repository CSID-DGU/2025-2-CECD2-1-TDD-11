package com.lifelibrarians.lifebookshelf.classification.repository;

import com.lifelibrarians.lifebookshelf.classification.domain.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    
    @Query("SELECT m FROM Material m WHERE m.chunk.category.autobiography.id = :autobiographyId AND m.chunk.category.theme.id = :themeId AND m.chunk.category.order = :categoryOrder AND m.chunk.order = :chunkOrder AND m.order = :materialOrder")
    Optional<Material> findByAutobiographyAndThemeAndOrdersAndMaterialOrder(@Param("autobiographyId") Long autobiographyId, @Param("themeId") Long themeId, @Param("categoryOrder") Integer categoryOrder, @Param("chunkOrder") Integer chunkOrder, @Param("materialOrder") Integer materialOrder);

    @Query("SELECT m FROM Material m " +
            "JOIN FETCH m.chunk ch " +
            "JOIN FETCH ch.category c " +
            "WHERE c.autobiography.id = :autobiographyId " +
            "ORDER BY m.count DESC")
    List<Material> findAllByAutobiographyIdOrderByCountDesc(@Param("autobiographyId") Long autobiographyId);
}
