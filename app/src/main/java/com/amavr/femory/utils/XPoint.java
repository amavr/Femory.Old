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
import java.util.ListIterator;

public class XPoint implements ListKeyHolder {

    private static final String APPKEY = "APPID";
    private static final String PREF = "femory.db";
    private static final String LISTKEY = "lists";
    private static final String TAG = "XDBG.XPoint";

    private static XPoint instance = null;
    private XStorage x;
    public String AppKey;

    private Gson gson = new Gson();
    /// В локальном хранилище лежат только ключи списков
    private SharedPreferences mPrefs = null;

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

//            Log.d(TAG, instance.mPrefs.getString(APPKEY, ""));
//            instance.delListKey("1f199ad587013357c93755ba10727519");
//            instance.mPrefs.edit().putString(LISTKEY, "[]").commit();

            /// ни одного списка еще нет
            if (!instance.mPrefs.contains(LISTKEY)) {
            }

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

    /// Получить все списки
    public List<ListInfo> getLists(){
        return x.getLists();
    }

    /// Получить все ключи списков из лок.хранилища
    public List<String> getListKeys(){
        String json = mPrefs.getString(LISTKEY, "[]");
        Gson gson = new Gson();
        String[] ids = gson.fromJson(json, String[].class);
        return new ArrayList<String>(Arrays.asList(ids));
    }

    /// сохранить список ключей в лок.хр
    private void saveListKeys(List<String> keys){
        mPrefs.edit().putString(LISTKEY, gson.toJson(keys)).commit();
        x.refreshList();
    }

    /// добавить ключ списка в лок.хр.
    public void addListKey(String key){
        List<String> keys = getListKeys();
        keys.add(key);
        saveListKeys(keys);
    }

    /// удалить ключ списка из лок.хр.
    public void delListKey(String key) {
        List<String> keys = getListKeys();
        keys.remove(key);
        saveListKeys(keys);
    }

    @Override
    public String getAppKey() {
        return AppKey;
    }
}
