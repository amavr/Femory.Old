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
    private HashMap<String, ListInfo> lists = new HashMap<>();
    private ListKeyHolder holder;

    private List<ListSubscriber> subscribers = new ArrayList<>();

    public XStorage(Context context, ListKeyHolder holder){
        this.context = context;
        this.holder = holder;

        for(String key: holder.getListKeys()){
            ListInfo li = new ListInfo();
            li.key = key;
            lists.putIfAbsent(key, li);
        }

        initFirebase();
    }

    private void initFirebase(){
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId(context.getString(R.string.goo_app_id)) // Required for Analytics.
                    .setApiKey(context.getString(R.string.api_key)) // Required for Auth.
                    .setDatabaseUrl(context.getString(R.string.db_url)) // Required for RTDB.
                    .build();
            FirebaseApp.initializeApp(context, options, "LISTS");

            /// список узлов БД по ID списков
            for(Map.Entry<String, ListInfo> pair: lists.entrySet()){
                ListInfo li = pair.getValue();
                Log.d(TAG, "list key: " + li.key);
                li.ref = createRef("lists/" + li.key, this);
            }


//            String ref_node = "lists/" + prefs.getString(APPKEY, "");
//            mRef = db.getReference(ref_node);
//            mRef.addValueEventListener(this);
//
//            Query query = mRef.orderByKey();
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    ListInfo li = dataSnapshot.getValue(ListInfo.class);
//                    if (li == null) {
//                        Log.d(TAG, "need to create");
//                        li = ListInfo.createMyList();
//                        mRef.setValue(li);
//                    }
//                    else{
//                        Log.d(TAG, "Not need to create");
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

//            ref.addValueEventListener(listener);
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

    public List<ListInfo> getLists(){
        List<ListInfo> list = new ArrayList<>();
        for(Map.Entry<String, ListInfo> pair: lists.entrySet()){
            ListInfo li = pair.getValue();
            list.add(pair.getValue());
        }
        return list;
    }

    /// поиск списка по названию (для пользователя)
    public ListInfo getListKey(String name){
        for(Map.Entry<String, ListInfo> pair: lists.entrySet()){
            ListInfo li = pair.getValue();
            if(li.name.equalsIgnoreCase(name)){
                return li;
            }
        }
        return null;
    }

    /// добавление в FB списка пользователем
    public ListInfo addNewList(String name){
        Log.d(TAG, String.format("addNewList: %s", name));

        ListInfo li = new ListInfo();
        li.name = name;
        li.key = Tools.generateKey();

        DatabaseReference ref = createRef("lists/" + li.key, this);
        li.ref = ref;
        ref.setValue(li);

        lists.put(li.key, li);
        holder.addListKey(li.key);
        return li;
    }

    public void delList(String key){
        Log.d(TAG, String.format("delList: %s", key));
        ListInfo li = lists.get(key);

        DatabaseReference ref = (DatabaseReference) li.ref;
        ref.removeEventListener(this);
        ref.removeValue();

        lists.remove(key);
        holder.delListKey(key);
    }

    public void updList(ListInfo li){
        Log.d(TAG, String.format("updList: %s", li.key));
        if(lists.containsKey(li.key)){
            lists.replace(li.key, li);
            ((DatabaseReference)li.ref).setValue(li);
        }
    }

    public void subscribe(ListSubscriber subscriber){
        Log.d(TAG, String.format("subscribe, key %s", subscriber.getKey()));
        /// при подписке сразу выдается что уже есть
        if(!subscribers.contains(subscriber)) {
            subscribers.add(subscriber);

            String sub_key = subscriber.getKey();
            for(Map.Entry<String, ListInfo> pair: lists.entrySet()){
                ListInfo li = pair.getValue();

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
            for(Map.Entry<String, ListInfo> pair: lists.entrySet()){
                ListInfo li = pair.getValue();

                if(sub_key == null || sub_key.equals(li.key)){
                    sub.onChange(li.key, li);
                }
            }
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        // This method is called once with the initial value and again
        // whenever data at this location is updated.
        try {
            String key = dataSnapshot.getKey();
            Log.d(TAG, "changed: "+ key);

            ListInfo li = dataSnapshot.getValue(ListInfo.class);
            if(li == null){
                delList(key);
            }
            else{
                li.key = key;
                li.ref = dataSnapshot.getRef();
                lists.replace(li.key, li);
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
