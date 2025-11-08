package com.lifelibrarians.lifebookshelf.member.service;

import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.MemberExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.mapper.MemberMapper;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.domain.MemberMetadata;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberBasicResponseDto;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberProfileResponseDto;
import com.lifelibrarians.lifebookshelf.member.repository.MemberMetadataRepository;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Logging
public class MemberQueryService {

	private final MemberMetadataRepository memberMetadataRepository;
	private final MemberRepository memberRepository;
	private final MemberMapper memberMapper;

	public MemberBasicResponseDto getMember(Long memberId) {
		MemberMetadata memberMetadata = memberMetadataRepository.findByMemberId(memberId)
				.orElseThrow(MemberExceptionStatus.MEMBER_METADATA_NOT_FOUND::toServiceException);

		return MemberBasicResponseDto.builder()
				.name(memberMetadata.getName())
				.bornedAt(memberMetadata.getBornedAt())
				.gender(memberMetadata.getGender())
				.hasChildren(memberMetadata.getHasChildren())
				.occupation(memberMetadata.getOccupation())
				.educationLevel(memberMetadata.getEducationLevel())
				.maritalStatus(memberMetadata.getMaritalStatus())
				.build();
	}

	public MemberProfileResponseDto getMemberProfile(Long memberId) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);
		return memberMapper.toMemberProfileResponseDto(member);
	}
}
