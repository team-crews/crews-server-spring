package com.server.crews.external.application;

import static com.slack.api.webhook.WebhookPayloads.payload;

import com.server.crews.global.CustomLogger;
import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SlackBugAlertService {
    private static final CustomLogger customLogger = new CustomLogger(SlackBugAlertService.class);
    private static final String RED_COLOR_CODE = "#ff0000";
    private static final String TITLE_PREFIX = "[Internal Server Error] ";

    private final String SLACK_WEBHOOK_URL;
    private final Slack slackClient;

    public SlackBugAlertService(@Value("${webhook.slack.bug.url}") String SLACK_WEBHOOK_URL) {
        this.SLACK_WEBHOOK_URL = SLACK_WEBHOOK_URL;
        this.slackClient = Slack.getInstance();
    }

    public void send(String title, Map<String, String> contents) {
        try {
            List<Field> fields = contents.entrySet()
                    .stream()
                    .map(entry -> generateField(entry.getKey(), entry.getValue()))
                    .toList();
            List<Attachment> attachments = List.of(Attachment.builder()
                    .color(RED_COLOR_CODE)
                    .fields(fields)
                    .build());

            slackClient.send(SLACK_WEBHOOK_URL,
                    payload(payloadBuilder -> payloadBuilder.text(TITLE_PREFIX + title).attachments(attachments))
            );
        } catch (Exception e) {
            customLogger.error(e);
        }
    }

    private Field generateField(String title, String value) {
        return Field.builder()
                .title(title)
                .value(value)
                .valueShortEnough(false)
                .build();
    }
}
