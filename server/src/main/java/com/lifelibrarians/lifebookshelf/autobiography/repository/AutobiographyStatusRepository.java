package com.lifelibrarians.lifebookshelf.autobiography.repository;

import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AutobiographyStatusRepository extends JpaRepository<AutobiographyStatus, Long> {
    // member id로 상태 조회
    List<AutobiographyStatus> findByMemberId(Long memberId);
}
