package com.shredder.mqtt;

import lombok.Getter;

public enum QualityOfService {
    FireAndForget(0),
    GuaranteedDelivery(1),
    GuaranteedOnceOnlyDelivery(2);

    @Getter
    private final int value;

    QualityOfService(int value) {
        this.value = value;
    }
}
