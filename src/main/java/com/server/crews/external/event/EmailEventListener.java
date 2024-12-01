package com.server.crews.external.event;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.event.OutcomeDeterminedEvent;
import com.server.crews.external.application.EmailService;
import com.server.crews.recruitment.domain.Recruitment;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class EmailEventListener {
    private final EmailService emailService;

    @TransactionalEventListener(value = OutcomeDeterminedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendEmailToApplicants(OutcomeDeterminedEvent event) {
        Recruitment recruitment = event.recruitment();
        List<Application> applications = event.applications();

        int middleIndex = applications.size() / 2;
        emailService.sendBatch(applications.subList(0, middleIndex), recruitment);
        emailService.sendBatch(applications.subList(middleIndex, applications.size()), recruitment);
    }
}
