package com.lifelibrarians.lifebookshelf.autobiography.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.exception.status.AutobiographyExceptionStatus;
import com.lifelibrarians.lifebookshelf.image.service.ImageService;
import com.lifelibrarians.lifebookshelf.log.Logging;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Logging
public class AutobiographyCommandService {

	private final AutobiographyRepository autobiographyRepository;
	private final ImageService imageService;
	@Value("${images.path.bio-cover}")
	public String BIO_COVER_IMAGE_DIR;

	public void patchAutobiography(Long memberId, Long autobiographyId, AutobiographyUpdateRequestDto requestDto) {
		Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
				.orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
		if (!autobiography.getMember().getId().equals(memberId)) {
			throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
		}
		String preSignedImageUrl = null;
		if (!Objects.isNull(requestDto.getPreSignedCoverImageUrl()) && !requestDto.getPreSignedCoverImageUrl().isBlank()) {
			preSignedImageUrl = imageService.parseImageUrl(requestDto.getPreSignedCoverImageUrl(), BIO_COVER_IMAGE_DIR);
		}
		autobiography.updateAutoBiography(requestDto.getTitle(), requestDto.getContent(), preSignedImageUrl, LocalDateTime.now());
	}

	public void deleteAutobiography(Long memberId, Long autobiographyId) {
		Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
				.orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
		if (!autobiography.getMember().getId().equals(memberId)) {
			throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
		}
		autobiographyRepository.delete(autobiography);
	}
}
