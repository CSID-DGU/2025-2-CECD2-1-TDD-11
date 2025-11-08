package com.lifelibrarians.lifebookshelf.member.service;

import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.MemberExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.domain.MemberMetadata;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberBasicV2ResponseDto;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Logging
public class MemberQueryServiceV2 {

	private final MemberRepository memberRepository;

	public MemberBasicV2ResponseDto getMember(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);
		
		MemberMetadata metadata = member.getMemberMemberMetadata();
		if (metadata == null) {
			throw MemberExceptionStatus.MEMBER_METADATA_NOT_FOUND.toServiceException();
		}

		return MemberBasicV2ResponseDto.builder()
			.name(metadata.getName())
			.bornedAt(metadata.getBornedAt())
			.gender(metadata.getGender())
			.hasChildren(metadata.getHasChildren())
			.occupation(metadata.getOccupation())
			.educationLevel(metadata.getEducationLevel())
			.maritalStatus(metadata.getMaritalStatus())
			.theme(metadata.getTheme())
			.ageGroup(metadata.getAgeGroup())
			.job(metadata.getJob())
			.whyCreate(metadata.getWhyCreate())
			.build();
	}
}