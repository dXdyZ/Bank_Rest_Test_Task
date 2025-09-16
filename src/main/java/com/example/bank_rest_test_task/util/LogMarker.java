package com.example.bank_rest_test_task.util;

import lombok.Getter;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@Getter
public enum LogMarker {
    AUDIT,
    LOGIN;

    private final Marker marker;

    LogMarker() {
        this.marker = MarkerFactory.getMarker(this.name());
    }
}
