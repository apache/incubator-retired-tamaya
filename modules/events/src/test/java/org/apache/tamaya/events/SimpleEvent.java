package org.apache.tamaya.events;

import org.apache.tamaya.events.spi.BaseConfigEvent;

import java.util.UUID;

public class SimpleEvent extends BaseConfigEvent<String> {

    public SimpleEvent(String paylod) {
        super(paylod, String.class);
    }

}