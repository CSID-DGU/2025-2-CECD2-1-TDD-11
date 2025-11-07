package com.lifelibrarians.lifebookshelf.publication;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lifelibrarians.lifebookshelf.auth.jwt.JwtTokenProvider;
import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.chapter.domain.Chapter;
import com.lifelibrarians.lifebookshelf.community.book.domain.Book;
import com.lifelibrarians.lifebookshelf.community.book.domain.BookChapter;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.domain.InterviewQuestion;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.publication.domain.Publication;
import com.lifelibrarians.lifebookshelf.publication.domain.PublishStatus;
import com.lifelibrarians.lifebookshelf.publication.dto.request.PublicationCreateRequestDto;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;
import utils.JsonMatcher;
import utils.PersistHelper;
import utils.test.E2EMvcTest;
import utils.testdouble.autobiography.TestAutobiography;
import utils.testdouble.book.TestBook;
import utils.testdouble.chapter.TestChapter;
import utils.testdouble.interview.TestInterview;
import utils.testdouble.interview.TestInterviewQuestion;
import utils.testdouble.member.TestMember;
import utils.testdouble.publication.TestPublicationCreateRequestDto;

public class PublicationControllerTest extends E2EMvcTest {

	private final String URL_PREFIX = "/api/v1/publications";
	private final String EMPTY_VALUE = "";
	private final String BEARER = "Bearer ";
	private final String AUTHORIZE_VALUE = "Authorization";

	private PersistHelper persistHelper;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	private String token;

	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext) {
		super.setup(webApplicationContext);
		this.persistHelper = PersistHelper.start(em);
	}

	@Nested
	@DisplayName("출판 요청 (POST /api/v1/publications)")
	class RequestPublication {

		private final String url = URL_PREFIX + "/";
		private Member loginMember;

		@BeforeEach
		void setUp() {
			loginMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			token = jwtTokenProvider.createMemberAccessToken(
					loginMember.getId()).getTokenValue();
		}

		@Test
		@DisplayName("실패 - 출판 책 제목이 제시되지 않음")
		void 실패_출판_책_제목이_제시되지_않음() throws Exception {
			// given
			PublicationCreateRequestDto publicationCreateRequestDto = TestPublicationCreateRequestDto
					.createNonBookTitlePublication();

			// when
			MockHttpServletRequestBuilder requestBuilder = multipart(url)
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
					.param("title", publicationCreateRequestDto.getTitle())
					.param("preSignedCoverImageUrl", publicationCreateRequestDto.getPreSignedCoverImageUrl())
					.param("titlePosition", publicationCreateRequestDto.getTitlePosition().name());
			ResultActions resultActions = mockMvc.perform(requestBuilder);

			// then
			JsonMatcher response = JsonMatcher.create();
			resultActions.andExpect(status().isBadRequest())
					.andExpect(response.get("code").isEquals("PUB003"))
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 출판 책 제목은 64자를 초과할 수 없음")
		void 실패_출판_책_제목은_64자를_초과할_수_없음() throws Exception {
			// given
			PublicationCreateRequestDto publicationCreateRequestDto = TestPublicationCreateRequestDto
					.createTooLongTitlePublication();

			// when
			MockHttpServletRequestBuilder requestBuilder = multipart(url)
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
					.param("title", publicationCreateRequestDto.getTitle())
					.param("preSignedCoverImageUrl", publicationCreateRequestDto.getPreSignedCoverImageUrl())
					.param("titlePosition", publicationCreateRequestDto.getTitlePosition().name());
			ResultActions resultActions = mockMvc.perform(requestBuilder);

			// then
			JsonMatcher response = JsonMatcher.create();
			resultActions.andExpect(status().isBadRequest())
					.andExpect(response.get("code").isEquals("PUB003"))
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 출판할 챕터는 최소 1개 이상이어야 함")
		void 실패_출판할_챕터는_최소_1개_이상이어야_함() throws Exception {
			// given
			PublicationCreateRequestDto publicationCreateRequestDto = TestPublicationCreateRequestDto
					.createValidPublication();

			// when
			MockHttpServletRequestBuilder requestBuilder = multipart(url)
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
					.param("title", publicationCreateRequestDto.getTitle())
					.param("preSignedCoverImageUrl", publicationCreateRequestDto.getPreSignedCoverImageUrl())
					.param("titlePosition", publicationCreateRequestDto.getTitlePosition().name());
			ResultActions resultActions = mockMvc.perform(requestBuilder);

			// then
			JsonMatcher response = JsonMatcher.create();
			resultActions.andExpect(status().isBadRequest())
					.andExpect(response.get("code").isEquals("PUB004"))
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 출판할 챕터 중 자서전이 없는 챕터가 존재하는 경우")
		void 실패_출판할_챕터_중_자서전이_없는_챕터가_존재하는_경우() throws Exception {
			// given
			List<Chapter> chapters = TestChapter.asDefaultEntities(loginMember);
			persistHelper.persist(chapters);
			List<Chapter> subchapters = new ArrayList<>();
			for (Chapter chapter : chapters) {
				subchapters.addAll(TestChapter.asDefaultSubchapterEntities(chapter, loginMember));
			}
			persistHelper.persistAndReturn(subchapters);

			PublicationCreateRequestDto publicationCreateRequestDto = TestPublicationCreateRequestDto
					.createValidPublication();

			// when
			MockHttpServletRequestBuilder requestBuilder = multipart(url)
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
					.param("title", publicationCreateRequestDto.getTitle())
					.param("preSignedCoverImageUrl", publicationCreateRequestDto.getPreSignedCoverImageUrl())
					.param("titlePosition", publicationCreateRequestDto.getTitlePosition().name());
			ResultActions resultActions = mockMvc.perform(requestBuilder);

			// then
			JsonMatcher response = JsonMatcher.create();
			resultActions.andExpect(status().isBadRequest())
					.andExpect(response.get("code").isEquals("PUB005"))
					.andDo(print());
		}

		@Test
		@DisplayName("성공 - 유효한 출판 요청 (Book, BookChapter, BookContent, Publication 생성, 관련 데이터들은 삭제)")
		void 성공_유효한_출판_요청() throws Exception {
			// given
			List<Chapter> chapters = TestChapter.asDefaultEntities(loginMember);
			persistHelper.persist(chapters);
			List<Chapter> subchapters = new ArrayList<>();
			for (Chapter chapter : chapters) {
				subchapters.addAll(TestChapter.asDefaultSubchapterEntities(chapter, loginMember));
			}
			List<Chapter> subChapterList = persistHelper.persistAndReturn(subchapters);
			for (Chapter subChapter : subChapterList) {
				Autobiography autobiography = persistHelper.persistAndReturn(
						TestAutobiography.asDefaultEntity(loginMember, subChapter));
				Interview interview = TestInterview.asDefaultEntity(
						autobiography,
						subChapter,
						loginMember,
						null
				);
				InterviewQuestion interviewQuestion = TestInterviewQuestion.asDefaultEntity(0, "질문1",
						interview);
				interviewQuestion.setInterview(interview);
				interview.setCurrentQuestion(interviewQuestion);
				interview.setQuestions(List.of(interviewQuestion));
				persistHelper.persist(interview);
			}
//			persistHelper.persist(autobiographies);

			PublicationCreateRequestDto publicationCreateRequestDto = TestPublicationCreateRequestDto
					.createValidPublication();

			// when
			MockHttpServletRequestBuilder requestBuilder = multipart(url)
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
					.param("title", publicationCreateRequestDto.getTitle())
					.param("preSignedCoverImageUrl", publicationCreateRequestDto.getPreSignedCoverImageUrl())
					.param("titlePosition", publicationCreateRequestDto.getTitlePosition().name());
			ResultActions resultActions = mockMvc.perform(requestBuilder);

			persistHelper.flushAndClear();

			// then
			TypedQuery<Chapter> chapterTypedQuery = em.createQuery(
					"SELECT c FROM Chapter c  WHERE c.member = :member", Chapter.class);
			List<Chapter> resultChapters = chapterTypedQuery.setParameter("member", loginMember)
					.getResultList();

			TypedQuery<Autobiography> autobiographyTypedQuery = em.createQuery(
					"SELECT a FROM Autobiography a WHERE a.member = :member", Autobiography.class);
			List<Autobiography> resultAutobiographies = autobiographyTypedQuery.setParameter("member",
							loginMember)
					.getResultList();

			TypedQuery<Interview> interviewTypedQuery = em.createQuery(
					"SELECT i FROM Interview i LEFT JOIN FETCH i.interviewConversations LEFT JOIN FETCH i.questions WHERE i.member = :member",
					Interview.class);
			List<Interview> resultInterviews = interviewTypedQuery.setParameter("member", loginMember)
					.getResultList();

			// 첫 번째 쿼리: Book과 BookChapters 로드
			TypedQuery<Book> bookQuery = em.createQuery("SELECT b FROM Book b "
					+ "LEFT JOIN FETCH b.bookChapters "
					+ "WHERE b.member = :member", Book.class);
			List<Book> resultBooks = bookQuery.setParameter("member", loginMember).getResultList();

// 두 번째 쿼리: BookChapters와 BookContents 로드
			for (Book book : resultBooks) {
				TypedQuery<BookChapter> bookChapterTypedQuery = em.createQuery(
						"SELECT bc FROM BookChapter bc "
								+ "LEFT JOIN FETCH bc.bookContents "
								+ "WHERE bc.book = :book", BookChapter.class);
				List<BookChapter> resultBookChapters = bookChapterTypedQuery.setParameter("book", book)
						.getResultList();
				book.setBookChapters(resultBookChapters);
			}

			TypedQuery<Publication> publicationTypedQuery = em.createQuery("SELECT p FROM Publication p "
					+ "LEFT JOIN FETCH p.book "
					+ "WHERE p.book.member = :member", Publication.class);
			Publication resultPublication = publicationTypedQuery.setParameter("member", loginMember)
					.getSingleResult();

			resultActions.andExpect(status().isCreated())
					.andDo(ignore -> {
						assertThat(resultChapters).isEmpty();
						assertThat(resultAutobiographies).isEmpty();
						assertThat(resultInterviews).isEmpty();
						for (Book book : resultBooks) {
							assertThat(book.getBookChapters().isEmpty()).isFalse();
							for (BookChapter bookChapter : book.getBookChapters()) {
								assertThat(bookChapter.getBookContents().isEmpty()).isFalse();
							}
							assertThat(resultPublication.getPublishStatus()).isEqualTo(PublishStatus.REQUESTED);
							assertThat(resultPublication.getTitlePosition()).isEqualTo(
									publicationCreateRequestDto.getTitlePosition());
						}
					})
					.andDo(print());
		}
	}

	@Nested
	@DisplayName("출판 책 삭제 (GET /api/v1/publications/{bookId})")
	class DeletePublication {

		private final String url = URL_PREFIX + "/";

		@BeforeEach
		void setUp() {
			token = jwtTokenProvider.createMemberAccessToken(
					persistHelper.persistAndReturn(TestMember.asDefaultEntity()).getId()).getTokenValue();
		}

		@Test
		@DisplayName("실패 - 출판 책이 존재하지 않는 경우, 삭제할 수 없음")
		void 실패_출판_책이_존재하지_않는_경우_삭제할_수_없음() throws Exception {
			// given
			long bookId = 99999L;

			// when
			MockHttpServletRequestBuilder requestBuilder = delete(url + bookId)
					.header(AUTHORIZE_VALUE, BEARER + token);
			ResultActions resultActions = mockMvc.perform(requestBuilder);

			// then
			JsonMatcher response = JsonMatcher.create();
			resultActions.andExpect(status().isNotFound())
					.andExpect(response.get("code").isEquals("COM001"))
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 자신의 출판 책이 아닌 경우, 삭제할 수 없음")
		void 실패_자신의_출판_책이_아닌_경우_삭제할_수_없음() throws Exception {
			// given
			Member otherMember = persistHelper.persistAndReturn(TestMember.asDefaultEntity());
			Book book = persistHelper.persistAndReturn(TestBook.asDefaultEntity(otherMember));

			// when
			MockHttpServletRequestBuilder requestBuilder = delete(url + book.getId())
					.header(AUTHORIZE_VALUE, BEARER + token);
			ResultActions resultActions = mockMvc.perform(requestBuilder);

			// then
			JsonMatcher response = JsonMatcher.create();
			resultActions.andExpect(status().isForbidden())
					.andExpect(response.get("code").isEquals("COM002"))
					.andDo(print());
		}
	}
}
