package com.server.crews.external.application;

import com.server.crews.applicant.domain.Applicant;
import com.server.crews.applicant.event.OutcomeDeterminedEvent;
import com.server.crews.recruitment.domain.Recruitment;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailEventListener {
    private final EmailService emailService;

    @EventListener(OutcomeDeterminedEvent.class)
    public void sendEmailToApplicants(final OutcomeDeterminedEvent event) {
        Recruitment recruitment = event.recruitment();
        List<Applicant> applicants = event.applicants();

        applicants.forEach(applicant -> emailService.send(applicant, recruitment));
    }
}
