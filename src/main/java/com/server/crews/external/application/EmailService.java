package com.server.crews.external.application;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.domain.Outcome;
import com.server.crews.global.CustomLogger;
import com.server.crews.recruitment.domain.Recruitment;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailService {
    private static final CustomLogger customLogger = new CustomLogger(EmailService.class);
    private static final String TITLE = "[Crews] 지원 결과 발표";

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Async("emailThreadPoolTaskExecutor")
    public void sendBatch(List<Application> applications, Recruitment recruitment) {
        List<MimeMessagePreparator> mimeMessagePreparators = applications.stream()
                .map(application -> createMessage(application, recruitment))
                .toList();
        javaMailSender.send(mimeMessagePreparators.toArray(new MimeMessagePreparator[0]));

        String applicationIds = applications.stream()
                .map(Application::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        customLogger.info("send email - recruitment id: {} application ids: {}", recruitment.getId(), applicationIds);
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
