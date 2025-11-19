package com.lifelibrarians.lifebookshelf.autobiography.repository;

import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AutobiographyStatusRepository extends JpaRepository<AutobiographyStatus, Long> {
    // member id로 상태 조회
    List<AutobiographyStatus> findByMemberId(Long memberId);

    // 선택한 type 중 가장 최근 상태 조회
    Optional<AutobiographyStatus> findTopByMemberIdAndStatusOrderByUpdatedAtDesc(Long memberId, AutobiographyStatusType status);

}
