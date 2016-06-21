package com.shredder.mqtt.app.fragments;

import com.shredder.mqtt.app.MqttSettingsPreferences;
import com.shredder.mqtt.MqttManagerConfig;
import com.shredder.mqtt.QualityOfService;

public class MqttClientFactory {
    public MqttManagerConfig createClient(final MqttSettingsPreferences preferences) {
        return new MqttManagerConfig() {
            @Override
            public String getHost() {
                return preferences.getHostName();
            }

            @Override
            public QualityOfService getQualityOfService() {
                //TODO return QualityOfService.(preferences.getQuality());
                return QualityOfService.FireAndForget;
            }

            @Override
            public String getUniqueId() {
                return preferences.getUniqueId();
            }
        };
    }
}
