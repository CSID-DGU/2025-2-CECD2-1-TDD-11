package com.lifelibrarians.lifebookshelf.member.service;

import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.dto.request.MemberUpdateV2RequestDto;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberBasicV2ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Logging
public class MemberFacadeServiceV2 {

	private final MemberQueryServiceV2 memberQueryServiceV2;
	private final MemberCommandServiceV2 memberCommandServiceV2;

	/*-----------------------------------------READ-----------------------------------------*/
	public MemberBasicV2ResponseDto getMember(Long memberId) {
		return memberQueryServiceV2.getMember(memberId);
	}

	/*-----------------------------------------CUD-----------------------------------------*/
	public void createMember(Long memberId, MemberUpdateV2RequestDto requestDto) {
		memberCommandServiceV2.createMember(memberId, requestDto);
	}

	public void updateMember(Long memberId, MemberUpdateV2RequestDto requestDto) {
		memberCommandServiceV2.updateMember(memberId, requestDto);
	}
}