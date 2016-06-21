package com.shredder.mqtt;

public interface MqttManagerConfig {
    String getHost();
    QualityOfService getQualityOfService();
    String getUniqueId();
}
