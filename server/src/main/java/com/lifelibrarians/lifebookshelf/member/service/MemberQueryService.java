package com.lifelibrarians.lifebookshelf.member.service;

import com.lifelibrarians.lifebookshelf.exception.status.MemberExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.domain.MemberMetadata;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberBasicResponseDto;
import com.lifelibrarians.lifebookshelf.member.repository.MemberMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Logging
public class MemberQueryService {

	private final MemberMetadataRepository memberMetadataRepository;

	public MemberBasicResponseDto getMember(Long memberId) {
		MemberMetadata memberMetadata = memberMetadataRepository.findByMemberId(memberId)
				.orElseThrow(MemberExceptionStatus.MEMBER_METADATA_NOT_FOUND::toServiceException);

		boolean isSuccessed = memberMetadata.getGender() != null 
				&& memberMetadata.getOccupation() != null 
				&& memberMetadata.getAgeGroup() != null;

		return MemberBasicResponseDto.builder()
				.gender(memberMetadata.getGender())
				.occupation(memberMetadata.getOccupation())
				.ageGroup(memberMetadata.getAgeGroup())
				.isSuccessed(isSuccessed)
				.build();
	}
}
