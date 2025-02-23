package com.CollegeManager.CollegeManagerServer.emailsender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;

    @Async
    public void sendEmail(
            String toEmail,
            String fullName,
            EmailTemplateName emailTemplateName,
            String activationCode,
            String subject
    ) throws MessagingException {

        MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
            @Override
            public void prepare(@NonNull MimeMessage mimeMessage) throws Exception {
                String htmlTemplate = emailTemplateName.getTemplateName();
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                        mimeMessage,
                        MULTIPART_MODE_MIXED,
                        UTF_8.name()
                );
                Map<String,Object> properties = new HashMap<>();
                properties.put("userName",fullName);
                properties.put("activationCode",activationCode);

                Context context = new Context();
                context.setVariables(properties);

                mimeMessageHelper.setTo(toEmail);
                mimeMessageHelper.setSubject(subject);

                String emailTemplate = springTemplateEngine.process(htmlTemplate,context);

                mimeMessageHelper.setText(emailTemplate,true);

                mimeMessageHelper.addInline("logo",new ClassPathResource("learn_free_logo.png"));
            }
        };

        javaMailSender.send(mimeMessagePreparator);
    }

}