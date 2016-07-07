package com.shredder.mqtt;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MqttSubscriptionMessage {
    private final String topic;
    private final String message;
}
