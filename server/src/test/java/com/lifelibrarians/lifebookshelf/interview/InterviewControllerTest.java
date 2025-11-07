package com.lifelibrarians.lifebookshelf.interview;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.chapter.domain.Chapter;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.domain.InterviewQuestion;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import com.lifelibrarians.lifebookshelf.auth.jwt.JwtTokenProvider;
import com.lifelibrarians.lifebookshelf.interview.dto.request.InterviewConversationCreateRequestDto;
import com.lifelibrarians.lifebookshelf.member.domain.Member;

import org.junit.jupiter.api.*;

import utils.JsonMatcher;
import utils.PersistHelper;
import utils.test.E2EMvcTest;
import utils.testdouble.autobiography.TestAutobiography;
import utils.testdouble.chapter.TestChapter;
import utils.testdouble.chapter.TestChapterStatus;
import utils.testdouble.interview.TestInterview;
import utils.testdouble.interview.TestInterviewConversationCreateRequestDto;
import utils.testdouble.interview.TestInterviewQuestion;
import utils.testdouble.member.TestMember;

public class InterviewControllerTest extends E2EMvcTest {

	private final String URL_PREFIX = "/api/v1/interviews";
	private final String EMPTY_VALUE = "";
	private final String BEARER = "Bearer ";
	private final String AUTHORIZE_VALUE = "Authorization";

	private PersistHelper persistHelper;

	private Interview createInterview(Member member, boolean isNoNextQuestion) {
		List<Chapter> chapters = TestChapter.asDefaultEntities(member);
		persistHelper.persist(chapters);
		List<Chapter> subchapters = new ArrayList<>();
		for (Chapter chapter : chapters) {
			subchapters.addAll(TestChapter.asDefaultSubchapterEntities(chapter, member));
		}
		List<Chapter> subChapterList = persistHelper.persistAndReturn(subchapters);
		Autobiography autobiography = persistHelper.persistAndReturn(
				TestAutobiography.asDefaultEntity(member, subChapterList.get(0)));
		persistHelper.persist(TestChapterStatus.asDefaultEntity(member, subChapterList.get(1)));
		Interview interview = TestInterview.asDefaultEntity(
				autobiography,
				subChapterList.get(0),
				member,
				null
		);
		List<InterviewQuestion> interviewQuestions = TestInterviewQuestion.asDefaultEntities(interview);
		for (InterviewQuestion interviewQuestion : interviewQuestions) {
			interviewQuestion.setInterview(interview);
		}
		interview.getQuestions().addAll(interviewQuestions);
		if (isNoNextQuestion) {
			interview.setCurrentQuestion(interviewQuestions.get(interviewQuestions.size() - 1));
		} else {
			interview.setCurrentQuestion(interviewQuestions.get(0));
		}
		return persistHelper.persistAndReturn(interview);
	}

	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	private String token;

	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext) {
		super.setup(webApplicationContext);
		this.persistHelper = PersistHelper.start(em);
	}

	@Nested
	@DisplayName("챗봇과의 대화 내역 전송 요청 (POST /api/v1/interviews/{interviewId}/conversations)")
	class SendInterviewConversations {

		private final String url = URL_PREFIX;
		private Member loginMember;

		@BeforeEach
		void setUp() {
			loginMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			token = jwtTokenProvider.createMemberAccessToken(
					loginMember.getId()).getTokenValue();
		}

		@Test
		@DisplayName("실패 - 인터뷰가 존재하지 않는 경우, 대화 내역을 전송할 수 없음")
		void 실패_인터뷰가_존재하지_않는_경우_대화_내역을_전송할_수_없음() throws Exception {
			// given
			InterviewConversationCreateRequestDto requestDto = TestInterviewConversationCreateRequestDto.createValidInterviewConversationCreateRequestDto();
			long wrongInterviewId = 99999L;

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + wrongInterviewId + "/conversations")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(requestDto));
			ResultActions resultActions = mockMvc.perform(requestBuilder);

			// then
			JsonMatcher response = JsonMatcher.create();
			resultActions
					.andExpect(status().isNotFound())
					.andExpect(response.get("code").isEquals("INTERVIEW001"))
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 자신의 인터뷰가 아닌 경우, 대화 내역을 전송할 수 없음")
		void 실패_자신의_인터뷰가_아닌_경우_대화_내역을_전송할_수_없음() throws Exception {
			// given
			InterviewConversationCreateRequestDto requestDto = TestInterviewConversationCreateRequestDto.createValidInterviewConversationCreateRequestDto();
			Member otherMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			Interview interview = createInterview(otherMember, false);

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + interview.getId() + "/conversations")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(requestDto));
			ResultActions resultActions = mockMvc.perform(requestBuilder);

			// then
			JsonMatcher response = JsonMatcher.create();
			resultActions
					.andExpect(status().isForbidden())
					.andExpect(response.get("code").isEquals("INTERVIEW002"))
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 대화는 20개를 초과하여 전송할 수 없음")
		void 실패_대화는_20개를_초과하여_전송할_수_없음() throws Exception {
			// given
			InterviewConversationCreateRequestDto requestDto = TestInterviewConversationCreateRequestDto.createTooManyConversationsInterviewConversationCreateRequestDto();
			Interview interview = createInterview(loginMember, false);

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + interview.getId() + "/conversations")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(requestDto));
			ResultActions resultActions = mockMvc.perform(requestBuilder);

			// then
			JsonMatcher response = JsonMatcher.create();
			resultActions
					.andExpect(status().isBadRequest())
					.andExpect(response.get("code").isEquals("INTERVIEW003"))
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 대화 내용은 512자를 초과하여 전송할 수 없음")
		void 실패_대화_내용은_512자를_초과하여_전송할_수_없음() throws Exception {
			// given
			InterviewConversationCreateRequestDto requestDto = TestInterviewConversationCreateRequestDto.createTooLongContentInterviewConversationCreateRequestDto();
			Interview interview = createInterview(loginMember, false);

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + interview.getId() + "/conversations")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(requestDto));
			ResultActions resultActions = mockMvc.perform(requestBuilder);

			// then
			JsonMatcher response = JsonMatcher.create();
			resultActions
					.andExpect(status().isBadRequest())
					.andExpect(response.get("code").isEquals("INTERVIEW004"))
					.andDo(print());
		}

		@Test
		@DisplayName("성공 - 유효한 대화 내역 전송 요청")
		void 성공_유효한_대화_내역_전송_요청() throws Exception {
			// given
			InterviewConversationCreateRequestDto requestDto = TestInterviewConversationCreateRequestDto.createValidInterviewConversationCreateRequestDto();
			Interview interview = createInterview(loginMember, false);

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + interview.getId() + "/conversations")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(requestDto));
			ResultActions resultActions = mockMvc.perform(requestBuilder);

			// then
			resultActions
					.andExpect(status().isCreated())
					.andDo(print());
		}
	}

	@Nested
	@DisplayName("현재 진행중인 인터뷰 질문을 다음 질문으로 갱신 요청 (POST /api/v1/interviews/{interviewId}/questions/current-question)")
	class UpdateCurrentQuestion {

		private final String url = URL_PREFIX;
		private Member loginMember;

		@BeforeEach
		void setUp() {
			loginMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			token = jwtTokenProvider.createMemberAccessToken(
					loginMember.getId()).getTokenValue();
		}

		@Test
		@DisplayName("실패 - 인터뷰가 존재하지 않는 경우, 다음 질문으로 갱신할 수 없음")
		void 실패_인터뷰가_존재하지_않는_경우_다음_질문으로_갱신할_수_없음() throws Exception {
			// given
			long wrongInterviewId = 99999L;

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + wrongInterviewId + "/questions/current-question")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON_VALUE);
			ResultActions resultActions = mockMvc.perform(requestBuilder);

			// then
			JsonMatcher response = JsonMatcher.create();
			resultActions
					.andExpect(status().isNotFound())
					.andExpect(response.get("code").isEquals("INTERVIEW001"))
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 자신의 인터뷰가 아닌 경우, 다음 질문으로 갱신할 수 없음")
		void 실패_자신의_인터뷰가_아닌_경우_다음_질문으로_갱신할_수_없음() throws Exception {
			// given
			Member otherMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			Interview interview = createInterview(otherMember, false);

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + interview.getId() + "/questions/current-question")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON_VALUE);
			ResultActions resultActions = mockMvc.perform(requestBuilder);

			// then
			JsonMatcher response = JsonMatcher.create();
			resultActions
					.andExpect(status().isForbidden())
					.andExpect(response.get("code").isEquals("INTERVIEW002"))
					.andDo(print());
		}

		@Test
		@DisplayName("실패 - 다음 질문이 존재하지 않는 경우, 다음 질문으로 갱신할 수 없음")
		void 실패_다음_질문이_존재하지_않는_경우_다음_질문으로_갱신할_수_없음() throws Exception {
			// given
			Interview interview = createInterview(loginMember, true);

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + interview.getId() + "/questions/current-question")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON_VALUE);
			ResultActions resultActions = mockMvc.perform(requestBuilder);

			// then
			JsonMatcher response = JsonMatcher.create();
			resultActions
					.andExpect(status().isNotFound())
					.andExpect(response.get("code").isEquals("INTERVIEW011"))
					.andDo(print());
		}

		@Test
		@DisplayName("성공 - 유효한 다음 질문으로 갱신 요청")
		void 성공_유효한_다음_질문으로_갱신_요청() throws Exception {
			// given
			Interview interview = createInterview(loginMember, false);

			// when
			MockHttpServletRequestBuilder requestBuilder = post(
					url + "/" + interview.getId() + "/questions/current-question")
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.APPLICATION_JSON_VALUE);
			ResultActions resultActions = mockMvc.perform(requestBuilder);

			// then
			TypedQuery<Interview> targetCheckQuery = em.createQuery(
					"SELECT i FROM Interview i JOIN FETCH i.currentQuestion WHERE i.id = :id",
					Interview.class);

			resultActions
					.andExpect(status().isOk())
					.andDo(ignore -> {
						Interview updatedInterview = targetCheckQuery
								.setParameter("id", interview.getId())
								.getSingleResult();
						assertThat(updatedInterview.getCurrentQuestion().getOrder())
								.isEqualTo(interview.getCurrentQuestion().getOrder() + 1);
					})
					.andDo(print());
		}
	}
}

