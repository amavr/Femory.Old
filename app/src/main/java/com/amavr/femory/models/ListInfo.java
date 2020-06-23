package com.amavr.femory.models;

import java.util.ArrayList;
import java.util.List;

public class ListInfo {

    public transient String key = null;
    public transient Object ref = null;

    public String name = null;
    public List<ItemInfo> items = new ArrayList<ItemInfo>();
}
