package com.server.crews.global.exception;

public record InternalErrorOccurredEvent(Exception exception, String uri) {
}
