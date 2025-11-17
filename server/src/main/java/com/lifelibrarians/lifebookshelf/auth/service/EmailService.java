package com.lifelibrarians.lifebookshelf.auth.service;

import com.lifelibrarians.lifebookshelf.log.Logging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Logging
public class EmailService {

	private final SesClient sesClient;
	private final Environment environment;

	@Value("${aws.ses.from-email:noreply@lifelibrarians.com}")
	private String fromEmail;

	private boolean isLocalProfile() {
		for (String profile : environment.getActiveProfiles()) {
			if ("local".equals(profile)) {
				return true;
			}
		}
		return false;
	}

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

		// 🔹 local 프로파일이면 SES 호출 스킵
		if (isLocalProfile()) {
			log.info("[LOCAL] Skip real SES send. to='{}', from='{}', subject='{}', body='{}'",
					toEmail, fromEmail, subject, body);
			return;
		}

		log.info("About to send email: to='{}', from='{}', subject='{}', body='{}'",
				toEmail, fromEmail, subject, body);

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

			log.debug("Built SendEmailRequest: {}", request);

			sesClient.sendEmail(request);

			log.info("Email successfully sent to '{}'", toEmail);

		} catch (SesException e) {
			log.error("SES sendEmail failed: {} (AWS Error Code: {}, Status Code: {})",
					e.awsErrorDetails().errorMessage(),
					e.awsErrorDetails().errorCode(),
					e.statusCode());
			throw new RuntimeException("이메일 전송 실패 (SES 예외 발생)", e);
		} catch (Exception e) {
			log.error("Unexpected error while sending email: {}", e.getMessage(), e);
			throw new RuntimeException("이메일 전송 실패 (기타 예외 발생)", e);
		}
	}
}
