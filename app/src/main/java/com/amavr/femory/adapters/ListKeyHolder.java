package com.amavr.femory.adapters;

import com.amavr.femory.models.ListInfo;

import java.util.List;

/// хранилище идентификаторов списков
public interface ListKeyHolder {
    List<String> getListKeys();
    void addListKey(String key);
    void delListKey(String key);
    String getAppKey();
}
