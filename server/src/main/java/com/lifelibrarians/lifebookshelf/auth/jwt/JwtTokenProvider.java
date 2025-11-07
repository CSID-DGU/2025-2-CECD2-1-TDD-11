package com.lifelibrarians.lifebookshelf.auth.jwt;

import com.lifelibrarians.lifebookshelf.config.JwtProperties;
import com.lifelibrarians.lifebookshelf.member.domain.MemberRole;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final JwtEncoder jwtEncoder;
	private final JwtProperties jwtProperties;
//	private final static String TOKEN_TYPE = "typ";

	/**
	 * memberId를 받아서 accessToken을 생성한다.
	 *
	 * @param memberId 회원 고유 ID
	 * @return accessToken
	 */
	public Jwt createMemberAccessToken(Long memberId) {
		Instant now = Instant.now();
		JwsHeader header = JwsHeader
				.with(MacAlgorithm.HS256)
//			.header(TOKEN_TYPE, TokenType.ACCESS)
				.build();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuedAt(now)
				.expiresAt(now.plusSeconds(jwtProperties.getAccessTokenExpirationTime()))
				.claim(JwtProperties.MEMBER_ID, memberId)
				.claim(JwtProperties.ROLES, List.of(MemberRole.MEMBER.name()))
				.build();
		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims));
	}
}