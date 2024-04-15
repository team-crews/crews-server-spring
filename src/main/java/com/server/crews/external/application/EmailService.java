package com.server.crews.external.application;

import com.server.crews.applicant.domain.Applicant;
import com.server.crews.applicant.domain.Outcome;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.recruitment.domain.Recruitment;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private static final String TITLE = "[Crews] 지원 결과 발표";
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    public void send(final Applicant applicant, final Recruitment recruitment) {
        MimeMessagePreparator message = createMessage(applicant, recruitment);

        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CrewsException(e.getMessage());
        }
    }

    private MimeMessagePreparator createMessage(final Applicant applicant, final Recruitment recruitment) {
        Context context = prepareVariables(applicant, recruitment);
        String htmlName = determineHtml(applicant.getOutcome());

        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(applicant.getEmail());
            helper.setSubject(TITLE);
            String htmlContent = templateEngine.process(htmlName, context);
            helper.setText(htmlContent, true);
        };

        return preparator;
    }

    private Context prepareVariables(final Applicant applicant, final Recruitment recruitment) {
        Context context = new Context();
        context.setVariables(Map.of(
                "club", recruitment.getClubName(),
                "name", applicant.getName(),
                "recruitment", recruitment.getTitle()));
        return context;
    }

    private String determineHtml(final Outcome outcome) {
        if (outcome.equals(Outcome.PASS)) {
            return "pass-email";
        }
        return "fail-email";
    }
}
