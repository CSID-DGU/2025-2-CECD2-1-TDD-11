package com.lifelibrarians.lifebookshelf.member.service;

import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.image.domain.ImageDeleteEvent;
import com.lifelibrarians.lifebookshelf.image.service.ImageService;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.dto.request.MemberProfileUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.domain.MemberMetadata;
import com.lifelibrarians.lifebookshelf.member.dto.request.MemberUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.member.repository.MemberMetadataRepository;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Logging
public class MemberCommandService {

	private final MemberRepository memberRepository;
	private final MemberMetadataRepository memberMetadataRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final ImageService imageService;
	@Value("${images.path.profile}")
	public String PROFILE_IMAGE_DIR;

	public void updateMember(Long memberId, MemberUpdateRequestDto requestDto) {

		LocalDateTime now = LocalDateTime.now();

		Member member = memberRepository.findByIdWithMetadata(memberId)
				.orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);

		if (member.getMemberMemberMetadata() == null) {
			MemberMetadata memberMetadata = MemberMetadata.of(
					requestDto.getName(),
					requestDto.getBornedAt(),
					requestDto.getGender(),
					requestDto.isHasChildren(),
					requestDto.getOccupation(),
					requestDto.getEducationLevel(),
					requestDto.getMaritalStatus(),
					now,
					now,
					member
			);

			memberMetadata = memberMetadataRepository.save(
					memberMetadata); // Save the MemberMetadata first
			member.setMemberMemberMetadata(memberMetadata);
		} else {
			member.getMemberMemberMetadata().update(
					requestDto.getName(),
					requestDto.getBornedAt(),
					requestDto.getGender(),
					requestDto.isHasChildren(),
					requestDto.getOccupation(),
					requestDto.getEducationLevel(),
					requestDto.getMaritalStatus()
			);
		}
	}

	public void updateMemberProfile(Long memberId, MemberProfileUpdateRequestDto requestDto) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);

		member.updateNickname(requestDto.getNickname(), LocalDateTime.now());

		if (Objects.isNull(requestDto.getPreSignedProfileImageUrl())
				|| requestDto.getPreSignedProfileImageUrl().isBlank()) {
			deletePreviousProfileImage(member);
			member.changeDefaultProfileImage();
		} else {
			String preSignedImageUrl = imageService.parseImageUrl(
					requestDto.getPreSignedProfileImageUrl(),
					PROFILE_IMAGE_DIR);
			// 기존 프로필 이미지와 같다면 업데이트 하지 않음
			if (member.isEqualProfileImageUrl(preSignedImageUrl)) {
				return;
			}
			// 기존 프로필 이미지 삭제
			deletePreviousProfileImage(member);
			member.updateProfileImageUrl(preSignedImageUrl);
		}
	}

	private void deletePreviousProfileImage(Member member) {
		String existingProfileFilename = member.getProfileImageUrl();
		if (Objects.nonNull(existingProfileFilename) && !existingProfileFilename.isBlank()) {
			eventPublisher.publishEvent(new ImageDeleteEvent(existingProfileFilename));
		}
	}
}
