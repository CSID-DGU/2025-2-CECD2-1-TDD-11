package com.lifelibrarians.lifebookshelf.member.service;

import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.domain.MemberMetadata;
import com.lifelibrarians.lifebookshelf.member.dto.request.MemberUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.member.repository.MemberMetadataRepository;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Logging
public class MemberCommandService {

	private final MemberRepository memberRepository;
	private final MemberMetadataRepository memberMetadataRepository;

	public void updateMember(Long memberId, MemberUpdateRequestDto requestDto) {
		Member member = memberRepository.findByIdWithMetadata(memberId)
				.orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);

		if (member.getMemberMemberMetadata() == null) {
			MemberMetadata memberMetadata = MemberMetadata.ofV2(
					"",
					requestDto.getGender(),
					requestDto.getAgeGroup(),
					requestDto.getOccupation(),
					member
			);
			memberMetadata = memberMetadataRepository.save(memberMetadata);
			member.setMemberMemberMetadata(memberMetadata);
		} else {
			member.getMemberMemberMetadata().updateV2(
					member.getMemberMemberMetadata().getName(),
					requestDto.getGender(),
					requestDto.getAgeGroup(),
					requestDto.getOccupation()
			);
		}
	}
}
