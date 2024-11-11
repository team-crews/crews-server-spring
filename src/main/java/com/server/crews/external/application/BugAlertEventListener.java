package com.server.crews.external.application;

import com.server.crews.global.exception.AsyncInternalErrorOccurredEvent;
import com.server.crews.global.exception.InternalErrorOccurredEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BugAlertEventListener {
    private final SlackBugAlertService slackBugAlertService;

    @Async
    @EventListener(value = InternalErrorOccurredEvent.class)
    public void alertWithSlack(InternalErrorOccurredEvent event) {
        Exception e = event.exception();

        Map<String, String> contents = new HashMap<>();
        contents.put("Error Message", e.getMessage());
        contents.put("URI", event.uri());

        slackBugAlertService.send(e.getClass().getSimpleName(), contents);
    }

    @EventListener(value = AsyncInternalErrorOccurredEvent.class)
    public void alertWithSlack(AsyncInternalErrorOccurredEvent event) {
        Exception e = event.exception();

        Map<String, String> contents = new HashMap<>();
        contents.put("Error Message", e.getMessage());

        slackBugAlertService.send(e.getClass().getSimpleName(), contents);
    }
}
