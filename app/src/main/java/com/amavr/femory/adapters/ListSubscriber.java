package com.amavr.femory.adapters;

import com.amavr.femory.models.ListInfo;

public interface ListSubscriber {
    void onChange(String key, ListInfo li);
    String getKey();
}
