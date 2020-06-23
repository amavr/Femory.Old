package com.amavr.femory.utils;

import com.amavr.femory.models.ListInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataEmitter {

    public static HashMap<String, ListInfo> getLists(int count){
        HashMap<String, ListInfo> dic = new HashMap<>();

        for(int i = 0; i < count; i++){
            ListInfo li = new ListInfo();
            li.name = String.format("Name %s", i + 1);
            dic.putIfAbsent(Tools.generateKey(), li);
        }

        return dic;
    }

}
