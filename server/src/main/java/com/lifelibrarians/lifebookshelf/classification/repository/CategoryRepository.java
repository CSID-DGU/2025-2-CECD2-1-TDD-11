package com.lifelibrarians.lifebookshelf.classification.repository;

import com.lifelibrarians.lifebookshelf.classification.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    @Query("SELECT c.name FROM Category c WHERE c.order = :categoryOrder")
    Optional<String> findNameByOrder(@Param("categoryOrder") Integer categoryOrder);
}
