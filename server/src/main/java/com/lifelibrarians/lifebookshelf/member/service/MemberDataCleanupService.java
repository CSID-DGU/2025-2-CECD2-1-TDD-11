package com.lifelibrarians.lifebookshelf.member.service;

import com.lifelibrarians.lifebookshelf.app.repository.MemberAppVersionRepository;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyChapterRepository;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyStatusRepository;
import com.lifelibrarians.lifebookshelf.chapter.repository.ChapterRepository;
import com.lifelibrarians.lifebookshelf.chapter.repository.ChapterStatusRepository;
import com.lifelibrarians.lifebookshelf.community.book.repository.BookChapterRepository;
import com.lifelibrarians.lifebookshelf.community.book.repository.BookContentRepository;
import com.lifelibrarians.lifebookshelf.community.book.repository.BookRepository;
import com.lifelibrarians.lifebookshelf.community.comment.repository.CommentRepository;
import com.lifelibrarians.lifebookshelf.interview.repository.ConversationRepository;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewQuestionRepository;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewRepository;
import com.lifelibrarians.lifebookshelf.member.repository.MemberMetadataRepository;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import com.lifelibrarians.lifebookshelf.member.repository.PasswordMemberRepository;
import com.lifelibrarians.lifebookshelf.notification.repository.DeviceRegistryRepository;
import com.lifelibrarians.lifebookshelf.notification.repository.NoticeHistoryRepository;
import com.lifelibrarians.lifebookshelf.notification.repository.NotificationSubscribeRepository;
import com.lifelibrarians.lifebookshelf.publication.repository.PublicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class MemberDataCleanupService {

	private final InterviewRepository interviewRepository;
	private final ConversationRepository conversationRepository;
	private final InterviewQuestionRepository interviewQuestionRepository;
	private final AutobiographyRepository autobiographyRepository;
	private final AutobiographyStatusRepository autobiographyStatusRepository;
	private final AutobiographyChapterRepository autobiographyChapterRepository;
	private final NotificationSubscribeRepository notificationSubscribeRepository;
	private final BookRepository bookRepository;
	private final BookChapterRepository bookChapterRepository;
	private final BookContentRepository bookContentRepository;
	private final PublicationRepository publicationRepository;
	private final CommentRepository commentRepository;
	private final NoticeHistoryRepository noticeHistoryRepository;
	private final MemberAppVersionRepository memberAppVersionRepository;
	private final ChapterRepository chapterRepository;
	private final ChapterStatusRepository chapterStatusRepository;
	private final MemberRepository memberRepository;
	private final MemberMetadataRepository memberMetadataRepository;
	private final PasswordMemberRepository passwordMemberRepository;
	private final DeviceRegistryRepository deviceRegistryRepository;

	/**
	 * 회원의 모든 데이터를 삭제합니다.
	 * Member, MemberMetadata, PasswordMember, DeviceRegistry를 제외한 모든 연관 데이터를 삭제합니다.
	 * 
	 * WARNING: findAll() 사용으로 인한 성능 이슈가 있습니다.
	 * 프로덕션 환경에서는 Repository에 쿼리 메서드 추가를 권장합니다.
	 * 
	 * @param memberId 회원 ID
	 */
	public void deleteAllMemberData(Long memberId) {
		log.info("[DELETE_ALL_DATA] 회원 데이터 전체 삭제 시작 - memberId: {}", memberId);

		// 1. Interview 관련 삭제
		deleteInterviewData(memberId);
		
		// 2. Autobiography 관련 삭제
		deleteAutobiographyData(memberId);
		
		// 3. Book 관련 삭제
		deleteBookData(memberId);
		
		// 4. 기타 데이터 삭제
		deleteOtherData(memberId);

		log.info("[DELETE_ALL_DATA] 회원 데이터 전체 삭제 완료 - memberId: {}", memberId);
	}

	private void deleteInterviewData(Long memberId) {
		List<Long> interviewIds = interviewRepository.findAll().stream()
				.filter(i -> i.getMember() != null && i.getMember().getId().equals(memberId))
				.map(i -> i.getId())
				.collect(Collectors.toList());

		if (interviewIds.isEmpty()) {
			log.info("[DELETE_ALL_DATA] Interview 데이터 없음 - memberId: {}", memberId);
			return;
		}

		// Conversation 삭제
		for (Long interviewId : interviewIds) {
			conversationRepository.deleteAll(
					conversationRepository.findAll().stream()
							.filter(c -> c.getInterview() != null && c.getInterview().getId().equals(interviewId))
							.collect(Collectors.toList())
			);
		}
		log.info("[DELETE_ALL_DATA] Conversation 삭제 완료 - memberId: {}", memberId);

		// InterviewQuestion 삭제
		for (Long interviewId : interviewIds) {
			interviewQuestionRepository.deleteAll(
					interviewQuestionRepository.findAll().stream()
							.filter(iq -> iq.getInterview() != null && iq.getInterview().getId().equals(interviewId))
							.collect(Collectors.toList())
			);
		}
		log.info("[DELETE_ALL_DATA] InterviewQuestion 삭제 완료 - memberId: {}", memberId);

		// Interview 삭제
		interviewRepository.deleteAll(
				interviewRepository.findAll().stream()
						.filter(i -> i.getMember() != null && i.getMember().getId().equals(memberId))
						.collect(Collectors.toList())
		);
		log.info("[DELETE_ALL_DATA] Interview 삭제 완료 - memberId: {}", memberId);
	}

	private void deleteAutobiographyData(Long memberId) {
		List<Long> autobiographyIds = autobiographyRepository.findAll().stream()
				.filter(a -> a.getMember() != null && a.getMember().getId().equals(memberId))
				.map(a -> a.getId())
				.collect(Collectors.toList());

		if (!autobiographyIds.isEmpty()) {
			// AutobiographyChapter 삭제
			for (Long autoId : autobiographyIds) {
				autobiographyChapterRepository.deleteAll(
						autobiographyChapterRepository.findAll().stream()
								.filter(ac -> ac.getAutobiography() != null && ac.getAutobiography().getId().equals(autoId))
								.collect(Collectors.toList())
				);
			}
			log.info("[DELETE_ALL_DATA] AutobiographyChapter 삭제 완료 - memberId: {}", memberId);
		}

		// AutobiographyStatus 삭제
		autobiographyStatusRepository.deleteAll(
				autobiographyStatusRepository.findAll().stream()
						.filter(as -> as.getMember() != null && as.getMember().getId().equals(memberId))
						.collect(Collectors.toList())
		);
		log.info("[DELETE_ALL_DATA] AutobiographyStatus 삭제 완료 - memberId: {}", memberId);

		// Autobiography 삭제
		if (!autobiographyIds.isEmpty()) {
			autobiographyRepository.deleteAll(
					autobiographyRepository.findAll().stream()
							.filter(a -> a.getMember() != null && a.getMember().getId().equals(memberId))
							.collect(Collectors.toList())
			);
			log.info("[DELETE_ALL_DATA] Autobiography 삭제 완료 - memberId: {}", memberId);
		}
	}

	private void deleteBookData(Long memberId) {
		List<Long> bookIds = bookRepository.findAll().stream()
				.filter(b -> b.getMember() != null && b.getMember().getId().equals(memberId))
				.map(b -> b.getId())
				.collect(Collectors.toList());

		if (bookIds.isEmpty()) {
			log.info("[DELETE_ALL_DATA] Book 데이터 없음 - memberId: {}", memberId);
			return;
		}

		// BookContent 삭제
		for (Long bookId : bookIds) {
			bookContentRepository.deleteAll(
					bookContentRepository.findAll().stream()
							.filter(bc -> bc.getBookChapter() != null && 
									bc.getBookChapter().getBook() != null &&
									bc.getBookChapter().getBook().getId().equals(bookId))
							.collect(Collectors.toList())
			);
		}
		log.info("[DELETE_ALL_DATA] BookContent 삭제 완료 - memberId: {}", memberId);

		// BookChapter 삭제
		for (Long bookId : bookIds) {
			bookChapterRepository.deleteAll(
					bookChapterRepository.findAll().stream()
							.filter(bc -> bc.getBook() != null && bc.getBook().getId().equals(bookId))
							.collect(Collectors.toList())
			);
		}
		log.info("[DELETE_ALL_DATA] BookChapter 삭제 완료 - memberId: {}", memberId);

		// Publication 삭제
		for (Long bookId : bookIds) {
			publicationRepository.deleteAll(
					publicationRepository.findAll().stream()
							.filter(p -> p.getBook() != null && p.getBook().getId().equals(bookId))
							.collect(Collectors.toList())
			);
		}
		log.info("[DELETE_ALL_DATA] Publication 삭제 완료 - memberId: {}", memberId);

		// Book 삭제
		bookRepository.deleteAll(
				bookRepository.findAll().stream()
						.filter(b -> b.getMember() != null && b.getMember().getId().equals(memberId))
						.collect(Collectors.toList())
		);
		log.info("[DELETE_ALL_DATA] Book 삭제 완료 - memberId: {}", memberId);
	}

	private void deleteOtherData(Long memberId) {
		// NotificationSubscribe 삭제
		notificationSubscribeRepository.deleteAll(
				notificationSubscribeRepository.findAll().stream()
						.filter(ns -> ns.getMember() != null && ns.getMember().getId().equals(memberId))
						.collect(Collectors.toList())
		);
		log.info("[DELETE_ALL_DATA] NotificationSubscribe 삭제 완료 - memberId: {}", memberId);

		// Comment 삭제
		commentRepository.deleteAll(
				commentRepository.findAll().stream()
						.filter(c -> c.getMember() != null && c.getMember().getId().equals(memberId))
						.collect(Collectors.toList())
		);
		log.info("[DELETE_ALL_DATA] Comment 삭제 완료 - memberId: {}", memberId);

		// NoticeHistory 삭제
		noticeHistoryRepository.deleteAll(
				noticeHistoryRepository.findAll().stream()
						.filter(nh -> nh.getMember() != null && nh.getMember().getId().equals(memberId))
						.collect(Collectors.toList())
		);
		log.info("[DELETE_ALL_DATA] NoticeHistory 삭제 완료 - memberId: {}", memberId);

		// MemberAppVersions 삭제
		memberAppVersionRepository.deleteAll(
				memberAppVersionRepository.findAll().stream()
						.filter(mav -> mav.getMember() != null && mav.getMember().getId().equals(memberId))
						.collect(Collectors.toList())
		);
		log.info("[DELETE_ALL_DATA] MemberAppVersions 삭제 완료 - memberId: {}", memberId);

		// Chapter 삭제 (Deprecated V1)
		chapterRepository.deleteAll(
				chapterRepository.findAll().stream()
						.filter(ch -> ch.getMember() != null && ch.getMember().getId().equals(memberId))
						.collect(Collectors.toList())
		);
		log.info("[DELETE_ALL_DATA] Chapter 삭제 완료 - memberId: {}", memberId);

		// ChapterStatus 삭제 (Deprecated V1)
		chapterStatusRepository.deleteAll(
				chapterStatusRepository.findAll().stream()
						.filter(cs -> cs.getMember() != null && cs.getMember().getId().equals(memberId))
						.collect(Collectors.toList())
		);
		log.info("[DELETE_ALL_DATA] ChapterStatus 삭제 완료 - memberId: {}", memberId);
	}

	/**
	 * 하드 탈퇴: Member, MemberMetadata, PasswordMember, DeviceRegistry까지 모두 삭제합니다.
	 * 회원의 모든 흔적을 완전히 제거합니다.
	 * 
	 * @param memberId 회원 ID
	 */
	public void hardDeleteMember(Long memberId) {
		log.info("[HARD_DELETE] 하드 탈퇴 시작 - memberId: {}", memberId);

		// 1. 먼저 모든 연관 데이터 삭제
		deleteAllMemberData(memberId);

		// 2. DeviceRegistry 삭제
		deviceRegistryRepository.deleteAll(
				deviceRegistryRepository.findAll().stream()
						.filter(dr -> dr.getMember() != null && dr.getMember().getId().equals(memberId))
						.collect(Collectors.toList())
		);
		log.info("[HARD_DELETE] DeviceRegistry 삭제 완료 - memberId: {}", memberId);

		// 3. MemberMetadata 삭제
		memberMetadataRepository.deleteAll(
				memberMetadataRepository.findAll().stream()
						.filter(mm -> mm.getMember() != null && mm.getMember().getId().equals(memberId))
						.collect(Collectors.toList())
		);
		log.info("[HARD_DELETE] MemberMetadata 삭제 완료 - memberId: {}", memberId);

		// 4. PasswordMember 삭제
		com.lifelibrarians.lifebookshelf.member.domain.Member member = memberRepository.findById(memberId).orElse(null);
		if (member != null && member.getPasswordMember() != null) {
			passwordMemberRepository.delete(member.getPasswordMember());
			log.info("[HARD_DELETE] PasswordMember 삭제 완료 - memberId: {}", memberId);
		}

		// 5. Member 삭제
		if (member != null) {
			memberRepository.delete(member);
			log.info("[HARD_DELETE] Member 삭제 완료 - memberId: {}", memberId);
		}

		log.info("[HARD_DELETE] 하드 탈퇴 완료 - memberId: {}", memberId);
	}
}
