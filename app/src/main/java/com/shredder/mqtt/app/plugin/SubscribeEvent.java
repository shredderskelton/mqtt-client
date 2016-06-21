package com.shredder.mqtt.app.plugin;

import lombok.Data;

@Data
public class SubscribeEvent {
    private final String topic;
}
