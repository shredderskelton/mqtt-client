package com.shredder.mqtt;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MqttPublishMessage {
    private final String topic;
    private final String message;
    private final boolean retain;
    private final QualityOfService qualityOfService;
}
