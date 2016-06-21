package com.shredder.mqtt.app.plugin;

import com.shredder.mqtt.MqttManager;
import com.shredder.mqtt.MqttManagerConfig;
import com.shredder.mqtt.app.MqttSettingsPreferences;
import com.shredder.mqtt.app.fragments.MqttClientFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MqttPluginController {

    private MqttManager mqttClient;
    private MqttManager.Listener listener = new MqttManager.Listener() {
        @Override
        public void onMessageReceived(String message, String topic) {
            EventBus.getDefault().post(new MessageReceivedEvent(message, topic));
        }
    };
    private final MqttSettingsPreferences prefs;

    public MqttPluginController(MqttSettingsPreferences prefs) {
        this.prefs = prefs;
        MqttManagerConfig config = new MqttClientFactory().createClient(prefs);
        mqttClient = new MqttManager(config, listener);
    }

    public void start() {
        EventBus.getDefault().register(this);
    }

    public void stop() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onPublish(PublishEvent event) {
        mqttClient.publish(event.getMessage(), event.getTopic());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onSubscribeEvent(SubscribeEvent event) {
        mqttClient.subscribe(event.getTopic());
    }

}
