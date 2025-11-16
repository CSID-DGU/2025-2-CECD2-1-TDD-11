package com.lifelibrarians.lifebookshelf.auth.service;

import com.lifelibrarians.lifebookshelf.log.Logging;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
@RequiredArgsConstructor
@Logging
public class EmailService {

	private final SesClient sesClient;

	@Value("${aws.ses.from-email:noreply@lifelibrarians.com}")
	private String fromEmail;

	public void sendVerificationCode(String toEmail, String code) {
		String subject = "[LifeLibrarians] 이메일 인증 코드";
		String body = String.format("인증 코드: %s\n\n5분 이내에 입력해주세요.", code);
		sendEmail(toEmail, subject, body);
	}

	public void sendTemporaryPassword(String toEmail, String tempPassword) {
		String subject = "[LifeLibrarians] 임시 비밀번호";
		String body = String.format("임시 비밀번호: %s\n\n로그인 후 비밀번호를 변경해주세요.", tempPassword);
		sendEmail(toEmail, subject, body);
	}

	private void sendEmail(String toEmail, String subject, String body) {
		try {
			SendEmailRequest request = SendEmailRequest.builder()
					.source(fromEmail)
					.destination(Destination.builder().toAddresses(toEmail).build())
					.message(Message.builder()
							.subject(Content.builder().data(subject).build())
							.body(Body.builder()
									.text(Content.builder().data(body).build())
									.build())
							.build())
					.build();

			sesClient.sendEmail(request);
		} catch (Exception e) {
			// TODO: 에러 처리 개선 필요
			throw new RuntimeException("이메일 전송 실패", e);
		}
	}
}
