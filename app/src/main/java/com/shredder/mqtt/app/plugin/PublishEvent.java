package com.shredder.mqtt.app.plugin;

import lombok.Data;

@Data
public class PublishEvent {
    private final String message;
    private final String topic;
}
