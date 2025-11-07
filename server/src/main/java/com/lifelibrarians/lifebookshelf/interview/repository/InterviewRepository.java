package com.lifelibrarians.lifebookshelf.interview.repository;

import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

	@Query("SELECT i FROM Interview i "
			+ "JOIN FETCH i.questions "
			+ "JOIN FETCH i.currentQuestion "
			+ "WHERE i.id = :interviewId")
	Optional<Interview> findWithQuestionsById(Long interviewId);

	// 특정 멤버의 인터뷰 개수 조회
	@Query("SELECT COUNT(i) FROM Interview i WHERE i.member.id = :memberId")
	Long countByMemberId(@Param("memberId") Long memberId);
}
