package com.shredder.mqtt.app.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.shredder.mqtt.app.MqttSettingsPreferences;
import com.shredder.mqtt.app.R;
import com.shredder.mqtt.app.base.BaseFragment;
import com.shredder.mqtt.app.plugin.MessageReceivedEvent;
import com.shredder.mqtt.app.plugin.PublishEvent;
import com.shredder.mqtt.app.plugin.SubscribeEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublisherFragment extends BaseFragment {

    public static final String TAG = "MAINTAG";
    @Bind(R.id.edit_message_text) EditText editTextMessage;
    @Bind(R.id.edit_message_topic) EditText editTextTopic;
    @Bind(R.id.textViewLog) TextView textLog;
    private MqttSettingsPreferences prefs;

    public static PublisherFragment newInstance() {
        return new PublisherFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        prefs = new MqttSettingsPreferences(getActivity());
        editTextTopic.setText(prefs.getTopic());
        return view;
    }

    @OnClick(R.id.button_send)
    public void onClick() {
        String topic = editTextTopic.getText().toString();
        prefs.setTopic(topic);
        EventBus.getDefault().post(new SubscribeEvent(topic));
        EventBus.getDefault().post(new PublishEvent(editTextMessage.getText().toString(), topic));
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        textLog.append(event.getMessage() + "\n");
    }

    @Override
    protected String getTitle() {
        return "Write message";
    }
}
