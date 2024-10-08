package com.server.crews.external.application;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.Outcome;
import com.server.crews.global.CustomLogger;
import com.server.crews.recruitment.domain.Recruitment;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailService {
    private static final String TITLE = "[Crews] 지원 결과 발표";
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final CustomLogger customLogger = new CustomLogger(EmailService.class);

    public void send(Application application, Recruitment recruitment) {
        customLogger.info("send - application id: {} recruitment id: {}", application.getId(), recruitment.getId());
        MimeMessagePreparator message = createMessage(application, recruitment);

        javaMailSender.send(message);
    }

    private MimeMessagePreparator createMessage(Application application, Recruitment recruitment) {
        Context context = prepareVariables(application, recruitment);
        String htmlName = determineHtml(application.getOutcome());

        return mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(application.getApplicant().getEmail());
            helper.setSubject(TITLE);
            String htmlContent = templateEngine.process(htmlName, context);
            helper.setText(htmlContent, true);
        };
    }

    private Context prepareVariables(Application application, Recruitment recruitment) {
        Context context = new Context();
        context.setVariables(Map.of(
                "name", application.getName(),
                "recruitment", recruitment.getTitle(),
                "club", recruitment.getPublisher().getClubName()));
        return context;
    }

    private String determineHtml(Outcome outcome) {
        if (outcome.equals(Outcome.PASS)) {
            return "pass-email";
        }
        return "fail-email";
    }
}
