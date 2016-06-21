package com.shredder.mqtt.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.shredder.mqtt.app.MqttSettingsPreferences;
import com.shredder.mqtt.app.R;
import com.shredder.mqtt.app.base.BackButtonSupportFragment;
import com.shredder.mqtt.app.base.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingsFragment extends BaseFragment implements BackButtonSupportFragment {
    private MqttSettingsPreferences preferences;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Bind(R.id.settings_host) EditText hostEditText;
    @Bind(R.id.settings_quality) EditText qualityEditText;
    @Bind(R.id.settings_topic) EditText topicEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        preferences = new MqttSettingsPreferences(getActivity());
        hostEditText.setText(preferences.getHostName());
        qualityEditText.setText(String.valueOf(preferences.getQuality()));
        topicEditText.setText(preferences.getTopic());
        return view;
    }

    @Override
    protected String getTitle() {
        return "SettingsFragment";
    }

    @Override
    public boolean onBackPressed() {
        preferences.setHostName(hostEditText.getText().toString());
        preferences.setQuality(Integer.valueOf(qualityEditText.getText().toString()));
        preferences.setTopic(topicEditText.getText().toString());
        return false;
    }
}
