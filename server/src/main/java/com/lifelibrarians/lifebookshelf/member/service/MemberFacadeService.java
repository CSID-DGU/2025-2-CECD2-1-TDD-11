package com.lifelibrarians.lifebookshelf.member.service;

import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.dto.request.MemberProfileUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.member.dto.request.MemberUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberBasicResponseDto;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Logging
public class MemberFacadeService {

	private final MemberQueryService memberQueryService;
	private final MemberCommandService memberCommandService;


	/*-----------------------------------------READ-----------------------------------------*/
	public MemberBasicResponseDto getMember(Long memberId) {
		return memberQueryService.getMember(memberId);
	}

	public MemberProfileResponseDto getMemberProfile(Long memberId) {
		return memberQueryService.getMemberProfile(memberId);
	}


	/*-----------------------------------------CUD-----------------------------------------*/
	public void updateMember(Long memberId, MemberUpdateRequestDto requestDto) {
		memberCommandService.updateMember(memberId, requestDto);
	}

	public void updateMemberProfile(Long memberId, MemberProfileUpdateRequestDto requestDto) {
		memberCommandService.updateMemberProfile(memberId, requestDto);
	}
}
