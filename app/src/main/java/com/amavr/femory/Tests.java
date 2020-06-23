package com.amavr.femory;

import android.content.Context;
import android.util.Log;

import com.amavr.femory.models.ListInfo;
import com.amavr.femory.utils.Tools;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class Tests {

    private static final String TAG = "XDBG.Tests";

    public static void initDB(Context context, ValueEventListener listener, String nodeId){
        try {
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setApplicationId("1:323447805848:android:3161966dfef174cc2d3ded") // Required for Analytics.
//                    .setApiKey("AIzaSyCM0Bhl0rz1sqjoNy-3ZAJz_AslYK06JNU") // Required for Auth.
//                    .setDatabaseUrl("https://femory-b01e1.firebaseio.com/") // Required for RTDB.
//                    .build();
//            FirebaseApp.initializeApp(context, options, "LISTS");
//            FirebaseDatabase db = FirebaseDatabase.getInstance();
//            DatabaseReference ref = db.getReference(nodeId);
//            ref.addValueEventListener(listener);
        }
        catch(Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    public static ListInfo generateList(Context context){
        String json = Tools.loadAssetAsString(context, "list.json");
        Gson gson = new Gson();
        return gson.fromJson(json, ListInfo.class);
    }

    public static void testWriteDB(String nodeId, ListInfo li){
        try{
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = db.getReference(nodeId);
            ref.setValue(li);
            Log.d(TAG, "Write success");
        }
        catch(Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }
}

