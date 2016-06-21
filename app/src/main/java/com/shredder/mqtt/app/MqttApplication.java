package com.shredder.mqtt.app;

import android.app.Application;

import com.shredder.mqtt.app.plugin.MqttPluginController;

public class MqttApplication extends Application {

    private MqttPluginController controller;

    @Override
    public void onCreate() {
        super.onCreate();
        controller = new MqttPluginController(new MqttSettingsPreferences(this));
        controller.start();
    }
}
