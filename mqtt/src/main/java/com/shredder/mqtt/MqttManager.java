package com.shredder.mqtt;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.List;

public class MqttManager {
    private final String TAG = "MqttManager";
    protected final MqttManagerConfig configuration;
    protected MqttClient client;
    private final Listener listener;
    private final List<String> subscriptions = new ArrayList<>();

    public MqttManager(MqttManagerConfig config, Listener listener) {
        this.configuration = config;
        this.listener = listener;
        try {
            Log.e(TAG, "Unique Id: " + config.getUniqueId());
            Log.e(TAG, "Host: " + config.getHost());
            client = new MqttClient(config.getHost(), config.getUniqueId(), new MemoryPersistence());
            client.setCallback(mqttCallback);
        } catch (MqttException e) {
            Log.e(TAG, "Exception setting up MQTT: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void connect() {
        if (client.isConnected()) {
            return;
        }
        MqttConnectOptions connectionOptions = new MqttConnectOptions();
        connectionOptions.setKeepAliveInterval(60 * 20);
        connectionOptions.setCleanSession(false);
        connectionOptions.setConnectionTimeout(30);
        try {
            client.connect(connectionOptions);
        } catch (MqttException e) {
            e.printStackTrace();
            Log.e(TAG, "connect() error" + e.getLocalizedMessage());
        }
    }

    public void publish(String message, String topic) {
        publish(message, topic, false);
    }

    public void publish(String message, String topic, boolean retain) {
        connect();
        try {
            Log.i(TAG, "publishing to " + topic + " with QOS: (" + configuration.getQualityOfService() + ") retaining: (" + (retain ? "yes" : "no") + ")");
            client.publish(topic, message.getBytes(), configuration.getQualityOfService().getValue(), retain);
        } catch (MqttException e) {
            Log.e(TAG, "Exception while publishing: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {
        try {
            connect();
            client.subscribe(topic);
            subscriptions.add(topic);
        } catch (MqttException e) {
            Log.e(TAG, "Exception while subscribing: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void destroy() {
        try {
            unsubscribeAll();
            client.disconnect(1000);
        } catch (MqttException e) {
            Log.e(TAG, "Exception while disconnecting: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void unsubscribeAll() {
        int maxLoops = subscriptions.size();
        while (maxLoops > 0 && subscriptions.size() > 0) {
            maxLoops--;
            unsubscribe(subscriptions.get(0));
        }
    }

    public void unsubscribe(String topic) {
        try {
            connect();
            client.unsubscribe(topic);
            subscriptions.remove(topic);
        } catch (MqttException e) {
            Log.e(TAG, "Exception while subscribing: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void connectionLost(Throwable cause) {
            Log.i(TAG, "Connection Lost. Resetting: Reason: " + cause.getLocalizedMessage());
            connect();
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.i(TAG, "Message Arrived: " + topic + " : " + message);
            messageReceived(new String(message.getPayload()), topic);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            try {
                Log.i(TAG, "Delivery Complete: " + token.getMessage().toString());
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    };

    protected void messageReceived(String message, String topic) {
        Log.i(TAG, "Message rx'd: " + topic + ":" + message);
        if (listener != null) {
            listener.onMessageReceived(message, topic);
        }
    }

    public interface Listener {
        void onMessageReceived(String message, String topic);
    }
}