package com.lifelibrarians.lifebookshelf.member.service;

import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.dto.request.MemberUpdateRequestDtoV2;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberBasicResponseDtoV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Logging
public class MemberFacadeServiceV2 {

	private final MemberQueryServiceV2 memberQueryServiceV2;
	private final MemberCommandServiceV2 memberCommandServiceV2;

	public MemberBasicResponseDtoV2 getMember(Long memberId) {
		return memberQueryServiceV2.getMember(memberId);
	}

	public void updateMember(Long memberId, MemberUpdateRequestDtoV2 requestDto) {
		memberCommandServiceV2.updateMember(memberId, requestDto);
	}
}
