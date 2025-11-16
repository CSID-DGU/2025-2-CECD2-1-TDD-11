package com.lifelibrarians.lifebookshelf.mapper;

import com.lifelibrarians.lifebookshelf.image.service.ImageService;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberDetailDto;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberPreviewDto;
import com.lifelibrarians.lifebookshelf.member.dto.response.MemberProfileResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class MemberMapper {

	@Autowired
	protected ImageService imageService;

	@Named("mapImageUrl")
	protected String mapImageUrl(String profileImageUrl) {
		return imageService.getImageUrl(profileImageUrl);
	}

	@Mapping(source = "member.id", target = "memberId")
	@Mapping(source = "member.profileImageUrl", target = "profileImageUrl", qualifiedByName = "mapImageUrl")
	public abstract MemberProfileResponseDto toMemberProfileResponseDto(Member member);

	@Mapping(source = "id", target = "memberId")
	@Mapping(source = "profileImageUrl", target = "profileImageUrl", qualifiedByName = "mapImageUrl")
	@Mapping(target = "metadata", ignore = true)
	@Mapping(target = "activitySummary", ignore = true)
	public abstract MemberPreviewDto toMemberPreviewDto(Member member);

	@Mapping(source = "id", target = "memberId")
	@Mapping(source = "profileImageUrl", target = "profileImageUrl", qualifiedByName = "mapImageUrl")
	public abstract MemberDetailDto toMemberDetailDto(Member member);
}
