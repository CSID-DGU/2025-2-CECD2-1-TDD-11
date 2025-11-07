package com.lifelibrarians.lifebookshelf.autobiography.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyCreateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.ChapterCreateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.chapter.domain.Chapter;
import com.lifelibrarians.lifebookshelf.chapter.domain.ChapterStatus;
import com.lifelibrarians.lifebookshelf.chapter.repository.ChapterRepository;
import com.lifelibrarians.lifebookshelf.chapter.repository.ChapterStatusRepository;
import com.lifelibrarians.lifebookshelf.exception.status.AutobiographyExceptionStatus;
import com.lifelibrarians.lifebookshelf.image.service.ImageService;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.domain.InterviewQuestion;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewQuestionRepository;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewRepository;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Logging
public class AutobiographyCommandService {

	private final ChapterRepository chapterRepository;
	private final ChapterStatusRepository chapterStatusRepository;
	private final AutobiographyRepository autobiographyRepository;
	private final InterviewQuestionRepository interviewQuestionRepository;
	private final InterviewRepository interviewRepository;
	private final ImageService imageService;
	@Value("${images.path.bio-cover}")
	public String BIO_COVER_IMAGE_DIR;

	public void createChapters(Member member, ChapterCreateRequestDto chapterCreateRequestDto) {

		List<Chapter> allByMemberId = chapterRepository.findAllByMemberId(member.getId());
		if (!allByMemberId.isEmpty()) {
			throw AutobiographyExceptionStatus.CHAPTER_ALREADY_EXISTS.toServiceException();
		}

		LocalDateTime now = LocalDateTime.now();
		List<Chapter> rootChapters = chapterCreateRequestDto.getChapters().stream()
				.map(chapterDto -> Chapter.of(chapterDto.getNumber(), chapterDto.getName(),
						chapterDto.getDescription(), now,
						null, member))
				.collect(Collectors.toList());

		chapterRepository.saveAll(rootChapters);

		List<Chapter> subChapters = chapterCreateRequestDto.getChapters().stream()
				.flatMap(chapterDto -> chapterDto.getSubchapters().stream()
						.map(subchapterDto -> {
							Long parentChapterId = rootChapters.stream()
									.filter(chapter -> chapter.getNumber()
											.equals(chapterDto.getNumber()))
									.findFirst()
									.orElseThrow(
											AutobiographyExceptionStatus.CHAPTER_NOT_FOUND::toServiceException)
									.getId();
							return Chapter.of(subchapterDto.getNumber(), subchapterDto.getName(),
									subchapterDto.getDescription(),
									now,
									parentChapterId, member);
						})
				)
				.collect(Collectors.toList());

		chapterRepository.saveAll(subChapters);

		// 서브 챕터 중 ID가 가장 작은 챕터를 찾음
		ChapterStatus chapterStatus = subChapters.stream()
				.min(Comparator.comparing(Chapter::getId))
				.map(chapter -> ChapterStatus.of(now, member, chapter))
				.orElseThrow(AutobiographyExceptionStatus.CHAPTER_NOT_FOUND::toServiceException);

		chapterStatusRepository.save(chapterStatus);
	}

	public void createAutobiography(Member member, AutobiographyCreateRequestDto requestDto,
			List<Chapter> chaptersNotRoot) {
		ChapterStatus chapterStatus = chapterStatusRepository.findByMemberId(
				member.getId()).orElseThrow(
				AutobiographyExceptionStatus.CHAPTER_NOT_FOUND::toServiceException);

		// parent_chapter_id가 null이 아닌 챕터만, 자서전이 작성되지 않은 챕터만 필터링
		List<Chapter> chapterFiltered = chaptersNotRoot.stream()
				.filter(chapter -> chapter.getId() >= chapterStatus.getCurrentChapter().getId())
				.filter(chapter -> chapter.getParentChapterId() != null)
				.filter(chapter -> member.getMemberAutobiographies().stream()
						.noneMatch(autobiography -> Objects.equals(autobiography.getChapter().getId(),
								chapter.getId())))
				.sorted(Comparator.comparing(Chapter::getId))
				.collect(Collectors.toList());

		if (chapterFiltered.isEmpty()) {
			throw AutobiographyExceptionStatus.NEXT_CHAPTER_NOT_FOUND.toServiceException();
		}

		// 자서전 생성
		Chapter currentChapter = chapterFiltered.get(0); // 현재 챕터는 필터링된 리스트의 첫 번째 챕터
		LocalDateTime now = LocalDateTime.now();
		String preSignedImageUrl = null;
		if (!Objects.isNull(requestDto.getPreSignedCoverImageUrl())
				&& !requestDto.getPreSignedCoverImageUrl().isBlank()) {
			preSignedImageUrl = imageService.parseImageUrl(
					requestDto.getPreSignedCoverImageUrl(),
					BIO_COVER_IMAGE_DIR);
		}

		Autobiography autobiography = Autobiography.of(
				requestDto.getTitle(),
				requestDto.getContent(),
				preSignedImageUrl,
				now,
				now,
				currentChapter,
				member
		);
		autobiographyRepository.save(autobiography);

		// 인터뷰 생성
		Interview interview = Interview.of(
				now,
				autobiography,
				currentChapter,
				member,
				null
		);
		interviewRepository.save(interview);

		// 인터뷰 질문 생성
		List<InterviewQuestion> interviewQuestions = requestDto.getInterviewQuestions().stream()
				.map(interviewDto -> InterviewQuestion.of(
						interviewDto.getOrder(),
						interviewDto.getQuestionText(),
						now,
						interview
				))
				.collect(Collectors.toList());

		interviewQuestionRepository.saveAll(interviewQuestions);

		// 인터뷰 업데이트 (현재 질문 설정)
		interview.setCurrentQuestion(interviewQuestions.get(0));
		interviewRepository.save(interview);
	}

	public void patchAutobiography(Long memberId, Long autobiographyId,
			AutobiographyUpdateRequestDto requestDto) {
		Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
				.orElseThrow(
						AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
		if (!autobiography.getMember().getId().equals(memberId)) {
			throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
		}
		String preSignedImageUrl = null;
		if (!Objects.isNull(requestDto.getPreSignedCoverImageUrl())
				&& !requestDto.getPreSignedCoverImageUrl().isBlank()) {
			preSignedImageUrl = imageService.parseImageUrl(
					requestDto.getPreSignedCoverImageUrl(),
					BIO_COVER_IMAGE_DIR);
		}
		autobiography.updateAutoBiography(requestDto.getTitle(), requestDto.getContent(),
				preSignedImageUrl, LocalDateTime.now());
	}

	public void deleteAutobiography(Long memberId, Long autobiographyId) {
		Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
				.orElseThrow(
						AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
		if (!autobiography.getMember().getId().equals(memberId)) {
			throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
		}
		autobiographyRepository.delete(autobiography);
	}

	public void updateCurrentChapter(Member member, List<Chapter> chaptersNotRoot) {
		ChapterStatus chapterStatus = chapterStatusRepository.findByMemberId(
				member.getId()).orElseThrow(
				AutobiographyExceptionStatus.CHAPTER_NOT_EXISTS::toServiceException);

		// 자서전이 존재하지 않는 경우, 다음 챕터를 찾을 수 없음
		List<Autobiography> autobiographies = autobiographyRepository.findByMemberId(member.getId());
		if (autobiographies.isEmpty()) {
			throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_EXISTS.toServiceException();
		}

		// parent_chapter_id가 null이 아닌 챕터만 필터링
		// 현재 챕터 이후의 챕터만 필터링
		List<Chapter> chapterFiltered = chaptersNotRoot.stream()
				.filter(chapter -> chapter.getId() > chapterStatus.getCurrentChapter().getId())
				.filter(chapter -> chapter.getParentChapterId() != null)
				.sorted(Comparator.comparing(Chapter::getId))
				.collect(Collectors.toList());

		if (chapterFiltered.isEmpty()) {
			throw AutobiographyExceptionStatus.NEXT_CHAPTER_NOT_FOUND.toServiceException();
		}

		Chapter currentChapter = chapterStatus.getCurrentChapter();

		// 다음 챕터를 찾아서 챕터 상태 업데이트
		Optional<Chapter> nextChapter = chapterFiltered.stream()
				.filter(chapter -> chapter.getId() > currentChapter.getId())
				.findFirst();

		if (nextChapter.isPresent()) {
			chapterStatus.updateChapter(nextChapter.get(), LocalDateTime.now());
			chapterStatusRepository.save(chapterStatus);
		}
	}
}
