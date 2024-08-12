package site.leesoyeon.probabilityrewardsystem.service;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import site.leesoyeon.probabilityrewardsystem.email.service.EmailService;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmailServiceIntegrationTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Test
    public void sendVerificationEmail_shouldSendRealEmail() throws MessagingException {
        // given
        String to = "wxy890@outlook.kr"; // 실제 이메일 주소
        String subject = "Email Verification";
        String text = "This is a test verification email.";

        // when
        emailService.sendVerificationEmail(to, subject, text);

        // then
        assertThat(javaMailSender).isNotNull();
    }
}
