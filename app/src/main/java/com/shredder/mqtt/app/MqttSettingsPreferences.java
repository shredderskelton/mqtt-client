package com.shredder.mqtt.app;

import android.content.Context;
import android.provider.Settings;

public class MqttSettingsPreferences extends com.shredder.utils.SharedPreferenceBase {
    private static final String HOST = "HOST";
    private static final String QOS = "QOS";
    private static final String TOPIC = "TOPIC";
    private static final String UNIQUE_ID = "UNIQUE_ID";

    public MqttSettingsPreferences(Context context) {
        super(context);
        String uniqueId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        setString(UNIQUE_ID, uniqueId);
    }

    @Override
    protected String getFileName() {
        return "settings";
    }

    public void setHostName(String hostName) {
        setString(HOST, hostName);
    }

    public String getHostName() {
        return getString(HOST, "tcp://broker.hivemq.com:1883");
    }

    public void setQuality(int quality) {
        setFloat(QOS, quality);
    }

    public int getQuality() {
        return (int) getFloat(QOS, 0);
    }

    public void setTopic(String topic) {
        setString(TOPIC, topic);
    }

    public String getTopic() {
        return getString(TOPIC, "nick");
    }

    public String getUniqueId(){
        return getString(UNIQUE_ID, "");
    }
}
