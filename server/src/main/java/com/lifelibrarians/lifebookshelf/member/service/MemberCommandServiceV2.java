package com.lifelibrarians.lifebookshelf.member.service;

import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.MemberExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.domain.MemberMetadata;
import com.lifelibrarians.lifebookshelf.member.dto.request.MemberUpdateV2RequestDto;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import com.lifelibrarians.lifebookshelf.member.repository.MemberMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Logging
public class MemberCommandServiceV2 {

	private final MemberRepository memberRepository;
	private final MemberMetadataRepository memberMetadataRepository;

	public void createMember(Long memberId, MemberUpdateV2RequestDto requestDto) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);
		
		MemberMetadata metadata = member.getMemberMemberMetadata();
		if (metadata == null) {
			// 새로 생성 - V1 필드는 기본값, V2 필드만 설정
			metadata = MemberMetadata.of(
					"기본이름", // name - 기본값
					java.time.LocalDate.of(1990, 1, 1), // bornedAt - 기본값
					requestDto.getGender(), // gender - V2에서 받음
					false, // hasChildren - 기본값
					null, // occupation - 기본값
					null, // educationLevel - 기본값
					null, // maritalStatus - 기본값
					LocalDateTime.now(), // createdAt
					LocalDateTime.now(), // updatedAt
					member,
					requestDto.getTheme(),
					requestDto.getAgeGroup(),
					requestDto.getJob(),
					requestDto.getWhyCreate()
			);
			metadata = memberMetadataRepository.save(metadata);
			member.setMemberMemberMetadata(metadata);
		} else {
			// 이미 존재하면 V2 필드만 업데이트
			metadata.updateV2(requestDto.getGender(), requestDto.getTheme(), 
						 requestDto.getAgeGroup(), requestDto.getJob(), requestDto.getWhyCreate());
		}
	}

	public void updateMember(Long memberId, MemberUpdateV2RequestDto requestDto) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);
		
		MemberMetadata metadata = member.getMemberMemberMetadata();
		if (metadata == null) {
			throw MemberExceptionStatus.MEMBER_METADATA_NOT_FOUND.toServiceException();
		}

		// Validation: gender 필수 검증은 @Valid에서 처리됨

		// Null 처리: whyCreate만 null 허용, 나머지는 기존값 유지
		String theme = requestDto.getTheme() != null ? requestDto.getTheme() : metadata.getTheme();
		String ageGroup = requestDto.getAgeGroup() != null ? requestDto.getAgeGroup() : metadata.getAgeGroup();
		String job = requestDto.getJob() != null ? requestDto.getJob() : metadata.getJob();
		String whyCreate = requestDto.getWhyCreate(); // null 허용

		// V2 필드 업데이트 + updatedAt 갱신
		metadata.updateV2(requestDto.getGender(), theme, ageGroup, job, whyCreate);
	}
}