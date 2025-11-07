package com.lifelibrarians.lifebookshelf.community.like.repository;

import com.lifelibrarians.lifebookshelf.community.like.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LikeRepository extends JpaRepository<Like, Long> {

}
