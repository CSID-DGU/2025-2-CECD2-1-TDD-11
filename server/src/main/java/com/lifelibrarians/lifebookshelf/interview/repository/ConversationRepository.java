package com.lifelibrarians.lifebookshelf.interview.repository;

import com.lifelibrarians.lifebookshelf.interview.domain.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ConversationRepository extends JpaRepository<Conversation, Long> {

	@Query("select c from Conversation c where c.interview.id = :interviewId")
	Page<Conversation> findAllByInterviewId(Long interviewId, Pageable pageable);
}
