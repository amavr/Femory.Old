package com.amavr.femory.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.amavr.femory.adapters.ListKeyHolder;
import com.amavr.femory.models.ListInfo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XPoint implements ListKeyHolder {

    private static final String APPKEY = "APPID";
    private static final String PREF = "femory.db";
    private static final String LISTKEY = "lists";
    private static final String TAG = "XDBG.XPoint";

    private Gson gson = new Gson();

    private static XPoint instance = null;
    private XStorage x;
    /// В локальном хранилище лежат только ключи списков и ключ приложения
    private SharedPreferences mPrefs = null;

    /// ключ приложения
    public String AppKey;
    /// Ключи списков
    public List<String> keys;

    public static XPoint create(Context context){
        if(instance == null) {
            instance = new XPoint();

            instance.mPrefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

            /// уникальный ключ приложения
            if (!instance.mPrefs.contains(APPKEY)) {
                String app_id = Tools.generateKey();
                instance.mPrefs.edit().putString(APPKEY, app_id).commit();
                Log.d(TAG, app_id);
            }
            instance.AppKey = instance.mPrefs.getString(APPKEY, "");

            String json = instance.mPrefs.getString(LISTKEY, "[]");
            String[] ids = instance.gson.fromJson(json, String[].class);
            instance.keys = new ArrayList<String>(Arrays.asList(ids));

            instance.x = new XStorage(context, instance);
        }
        return instance;
    }

    private XPoint(){}

    public XStorage getStorage(){
        return x;
    }

    public static XPoint getInstance(){
        return instance;
    }

    /// Получить все ключи списков из лок.хранилища
    public List<String> getListKeys(){
        return this.keys;
    }

    /// сохранить список ключей в лок.хр
    private void saveKeys(){
        mPrefs.edit().putString(LISTKEY, gson.toJson(this.keys)).commit();
    }

    /// добавить ключ списка в лок.хр.
    public void addListKey(String key){
        keys.add(key);
        saveKeys();
    }

    /// удалить ключ списка из лок.хр.
    public void delListKey(String key) {
        keys.remove(key);
        saveKeys();
    }

    @Override
    public String getAppKey() {
        return AppKey;
    }
}
