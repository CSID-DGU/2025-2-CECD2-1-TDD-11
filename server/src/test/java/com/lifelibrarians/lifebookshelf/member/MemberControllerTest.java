package com.lifelibrarians.lifebookshelf.member;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lifelibrarians.lifebookshelf.member.domain.MemberMetadata;
import com.lifelibrarians.lifebookshelf.member.dto.request.MemberUpdateRequestDto;
import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;


import com.lifelibrarians.lifebookshelf.auth.jwt.JwtTokenProvider;
import com.lifelibrarians.lifebookshelf.member.domain.Member;

import utils.JsonMatcher;
import utils.PersistHelper;
import utils.test.E2EMvcTest;
import utils.testdouble.member.TestMember;
import utils.testdouble.member.TestMemberMetadata;

public class MemberControllerTest extends E2EMvcTest {

	private final String URL_PREFIX = "/api/v1/members";
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
	@DisplayName("회원 정보 수정 (PUT /api/v1/members/me)")
	class UpdateMember {

		private final String url = URL_PREFIX + "/me";
		private Member loginMember;

		@BeforeEach
		void setUp() {
			loginMember = persistHelper
					.persistAndReturn(TestMember.asDefaultEntity());
			token = jwtTokenProvider.createMemberAccessToken(
					loginMember.getId()).getTokenValue();
		}

		@Test
		@DisplayName("실패 - 이름은 64자를 초과할 수 없음.")
		void 실패_이름은_64자를_초과할_수_없음() throws Exception {
			// given
			MemberMetadata memberMetadata = persistHelper.persistAndReturn(
					TestMemberMetadata.asDefaultEntity(loginMember));

			String tooLongName = "a".repeat(65);
			MemberUpdateRequestDto requestDto = MemberUpdateRequestDto.builder()
					.name(tooLongName)
					.bornedAt(memberMetadata.getBornedAt())
					.gender(memberMetadata.getGender())
					.hasChildren(memberMetadata.getHasChildren())
					.occupation(memberMetadata.getOccupation())
					.educationLevel(memberMetadata.getEducationLevel())
					.maritalStatus(memberMetadata.getMaritalStatus())
					.build();

			// when
			MockHttpServletRequestBuilder requestBuilder = multipart(HttpMethod.PUT, url)
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
					.param("name", requestDto.getName())
					.param("bornedAt", requestDto.getBornedAt().toString())
					.param("gender", requestDto.getGender().toString())
					.param("hasChildren", String.valueOf(requestDto.isHasChildren()))
					.param("occupation", requestDto.getOccupation())
					.param("educationLevel", requestDto.getEducationLevel())
					.param("maritalStatus", requestDto.getMaritalStatus());

			ResultActions resultActions = mockMvc.perform(requestBuilder);

			// then
			JsonMatcher response = JsonMatcher.create();
			resultActions.andExpect(status().isBadRequest())
					.andExpect(response.get("code").isEquals("MEMBER001"))
					.andDo(print());
		}

		@Test
		@DisplayName("성공 - 유효한 회원 정보 수정")
		void 성공_유효한_회원_정보_수정() throws Exception {
			// given
			MemberMetadata memberMetadata = persistHelper.persistAndReturn(
					TestMemberMetadata.asDefaultEntity(loginMember));

			String newName = "John Doe";
			MemberUpdateRequestDto requestDto = MemberUpdateRequestDto.builder()
					.name(newName)
					.bornedAt(memberMetadata.getBornedAt())
					.gender(memberMetadata.getGender())
					.hasChildren(memberMetadata.getHasChildren())
					.occupation(memberMetadata.getOccupation())
					.educationLevel(memberMetadata.getEducationLevel())
					.maritalStatus(memberMetadata.getMaritalStatus())
					.build();

			persistHelper.persistAndReturn(TestMemberMetadata.asDefaultEntity(loginMember));

			// when
			MockHttpServletRequestBuilder requestBuilder = multipart(HttpMethod.PUT, url)
					.header(AUTHORIZE_VALUE, BEARER + token)
					.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
					.param("name", requestDto.getName())
					.param("bornedAt", requestDto.getBornedAt().toString())
					.param("gender", requestDto.getGender().toString())
					.param("hasChildren", String.valueOf(requestDto.isHasChildren()))
					.param("occupation", requestDto.getOccupation())
					.param("educationLevel", requestDto.getEducationLevel())
					.param("maritalStatus", requestDto.getMaritalStatus());

			ResultActions resultActions = mockMvc.perform(requestBuilder);

			// then
			resultActions.andExpect(status().isOk())
					.andDo(print());
		}
	}
}
