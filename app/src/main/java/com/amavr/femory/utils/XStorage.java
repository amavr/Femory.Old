package com.amavr.femory.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.amavr.femory.R;
import com.amavr.femory.adapters.ListKeyHolder;
import com.amavr.femory.adapters.ListSubscriber;
import com.amavr.femory.models.ListInfo;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XStorage implements ValueEventListener {

    private static final String TAG = "XDBG.XStorage";

    private Context context;
    private Gson gson = new Gson();
    private HashMap<String, ListInfo> lists = new HashMap<>();;
    private ListKeyHolder holder;

    private List<ListSubscriber> subscribers = new ArrayList<>();

    public XStorage(Context context, ListKeyHolder holder){
        this.context = context;
        this.holder = holder;

        initFirebase();

        /// запрос списков в FB по их ключам
        for(String key: holder.getListKeys()){
            queryListByKey(key);
//            lists.put(key, queryListByKey(key));
        }
    }

    private void initFirebase(){
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId(context.getString(R.string.goo_app_id)) // Required for Analytics.
                    .setApiKey(context.getString(R.string.api_key)) // Required for Auth.
                    .setDatabaseUrl(context.getString(R.string.db_url)) // Required for RTDB.
                    .build();
            FirebaseApp.initializeApp(context, options, "LISTS");
        }
        catch(Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    private DatabaseReference createRef(String path, ValueEventListener listener){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference(path);
        ref.addValueEventListener(listener);
        return ref;
    }

    public ListInfo queryListByKey(String key){
        ListInfo li = new ListInfo();
        li.key = key;
        li.ref = createRef("lists/" + key, this);
        return li;
    }

    /// Списки
    public List<ListInfo> getLists(){
        return new ArrayList<>(lists.values());
    }

    /// добавление в FB списка пользователем
    public void addListToFB(String name){
        Log.d(TAG, String.format("addNewList: %s", name));

        String key = String.format("%s:%s", holder.getAppKey(), Tools.generateKey());

        ListInfo li = new ListInfo();
        li.name = name;
        li.key = key;

        /// сохранить в FB и подписаться на изменения
        DatabaseReference ref = createRef("lists/" + key, this);
        li.ref = ref;
        ref.setValue(li);

        /// добавление списка-объекта
        lists.put(key, li);

        /// добавление ключа списка в локальное хранилище
        holder.addListKey(li.key);
    }

    public void delListFromFB(String key){
        Log.d(TAG, String.format("delList: %s", key));
        ListInfo li = lists.get(key);

        DatabaseReference ref = (DatabaseReference) li.ref;

        /// удаление уведомления на изменение списка
        ref.removeEventListener(this);

        /// из базы удаляется только если это список автора (начинается с его ключа)
        if(key.startsWith(holder.getAppKey())) {
            ref.removeValue();
        }

        /// удаление списка-объекта
        lists.remove(key);

        /// удаление ключа списка из локального хранилища
        holder.delListKey(key);

        /// TODO: оповестить подписчиков, что список удален

    }

    public void updListAtFB(ListInfo li){
        Log.d(TAG, String.format("updList: %s", li.key));
        ((DatabaseReference)li.ref).setValue(li);
        if(lists.containsKey(li.key)){
            lists.replace(li.key, li);
        }
    }

    public void subscribe(ListSubscriber subscriber){
        Log.d(TAG, String.format("subscribe, key %s", subscriber.getKey()));
        /// при подписке сразу выдается что уже есть
        if(!subscribers.contains(subscriber)) {
            subscribers.add(subscriber);

            String sub_key = subscriber.getKey();
            for(ListInfo li: getLists()){
                if(sub_key == null || sub_key.equals(li.key)){
                    subscriber.onChange(li.key, li);
                }
            }
        }
    }

    public void unsubscribe(ListSubscriber subscriber){
        Log.d(TAG, String.format("unsubscribe, key: %s", subscriber.getKey()));
        subscribers.remove(subscriber);
    }

    public void notifySubs(){
        Log.d(TAG, "notify all");

        for(ListSubscriber sub: subscribers){
            String sub_key = sub.getKey();
            for(ListInfo li: getLists()){
                if(sub_key == null || sub_key.equals(li.key)){
                    sub.onChange(li.key, li);
                }
            }
        }
    }

    /// получение ответа по запрошенному по ключу списку
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        try {
            String key = dataSnapshot.getKey();
            Log.d(TAG, "changed: "+ key);

            ListInfo li = dataSnapshot.getValue(ListInfo.class);
            /// в базе не найден, значит удалить с клиента
            if(li == null){
                /// удаление списка-объекта
                if(lists.containsKey(key)){
                    lists.remove(key);
                }
                /// удаление ключа списка
                holder.delListKey(key);
            }
            else{
                li.key = key;
                li.ref = dataSnapshot.getRef();
                if(lists.containsKey(key)){
                    lists.replace(li.key, li);
                }
                else{
                    lists.put(key, li);
                }
            }

            for(ListSubscriber sub: subscribers){
                String sub_key = sub.getKey();
                if(sub_key == null || sub_key.equals(key)){
                    sub.onChange(key, li);
                }
            }

            Log.d(TAG, "Value is: " + gson.toJson(li));
        }
        catch(Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        // Failed to read value
        Log.w(TAG, "Failed to read value.", databaseError.toException());
    }
}
