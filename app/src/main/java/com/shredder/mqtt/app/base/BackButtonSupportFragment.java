package com.shredder.mqtt.app.base;

public interface BackButtonSupportFragment {
    // return true if your fragment has consumed the back press event, false if you don't care about it
    boolean onBackPressed();
}
