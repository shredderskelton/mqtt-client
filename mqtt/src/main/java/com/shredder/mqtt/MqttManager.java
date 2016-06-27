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
    private static final int MAX_BACKOFF = 10000; //ten seconds
    private final String TAG = "MqttManager";
    protected final MqttManagerConfig configuration;
    protected MqttClient client;
    private final Listener listener;
    private final List<String> subscriptions = new ArrayList<>();
    private boolean isConnecting = false;
    private boolean isRunning = false;
    private int backoff = 0;

    public MqttManager(MqttManagerConfig config, Listener listener) {
        this.configuration = config;
        this.listener = listener;
        try {
            Log.d(TAG, "Unique Id: " + config.getUniqueId());
            Log.d(TAG, "Host: " + config.getHost());
            client = new MqttClient(config.getHost(), config.getUniqueId(), new MemoryPersistence());
            client.setCallback(mqttCallback);
        } catch (MqttException e) {
            Log.e(TAG, "Exception setting up MQTT: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void backoffConnect() {
        backoff++;
        if (isConnecting) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                isConnecting = true;
                try {
                    long exponentialBackoff = 100 * (long) Math.exp(backoff);
                    long backOffInMillis = Math.min(MAX_BACKOFF, exponentialBackoff);
                    Log.v(TAG, "Backing off for " + backOffInMillis);
                    Thread.sleep(backOffInMillis);
                } catch (InterruptedException e) {
                    exit(false);
                }
                if (!isRunning) {
                    exit(false);
                }
                exit(true);
            }

            private void exit(boolean withConnect) {
                isConnecting = false;
                if (withConnect) {
                    connect();
                }
            }
        }).start();
    }

    private synchronized void connect() {
        if (client.isConnected()) {
            return;
        }
        MqttConnectOptions connectionOptions = new MqttConnectOptions();
        connectionOptions.setKeepAliveInterval(60 * 20);
        connectionOptions.setCleanSession(false);
        connectionOptions.setConnectionTimeout(30);
        try {
            client.connect(connectionOptions);
            Log.v(TAG, "Connect succeeded. Resetting backoff period and resubscribing.");
            resubscribe();
            backoff = 0;
        } catch (MqttException e) {
            e.printStackTrace();
            Log.e(TAG, "connect() error" + e.getLocalizedMessage());
            backoffConnect();
        }

    }

    public void publish(String message, String topic) {
        publish(message, topic, false);
    }

    public void publish(String message, String topic, boolean retain) {
        try {
            Log.i(TAG, "publishing to " + topic + " with QOS: (" + configuration.getQualityOfService() + ") retaining: (" + (retain ? "yes" : "no") + ")");
            client.publish(topic, message.getBytes(), configuration.getQualityOfService().getValue(), retain);
        } catch (MqttException e) {
            Log.e(TAG, "Exception while publishing: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void clearRetained(String topic){
        try {
            Log.i(TAG, "Clearing retained messages for " + topic);
            client.publish(topic, new byte[0], configuration.getQualityOfService().getValue(), false);
        } catch (MqttException e) {
            Log.e(TAG, "Exception while publishing: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {
        if (!subscriptions.contains(topic)) {
            subscriptions.add(topic);
        }
        resubscribe();
    }

    private synchronized void resubscribe() {
        for (String topic : subscriptions) {
            try {
                client.subscribe(topic);
            } catch (MqttException e) {
                Log.e(TAG, "Exception while subscribing: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }

    public void start() {
        isRunning = true;
        backoffConnect();
    }

    public void stop() {
        try {
            isRunning = false;
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
            backoffConnect();
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
            backoffConnect();
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