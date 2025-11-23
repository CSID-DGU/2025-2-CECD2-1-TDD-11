package com.lifelibrarians.lifebookshelf.classification.repository;

import com.lifelibrarians.lifebookshelf.classification.domain.Theme;
import com.lifelibrarians.lifebookshelf.classification.domain.ThemeNameType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ThemeRepository extends JpaRepository<Theme, Long> {
    
    @Query("SELECT t FROM Theme t JOIN FETCH t.categories WHERE t.name = :name")
    Optional<Theme> findByNameWithCategories(@Param("name") ThemeNameType name);
}
