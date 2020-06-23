package com.amavr.femory;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amavr.femory.adapters.ListSubscriber;
import com.amavr.femory.models.ListInfo;
import com.amavr.femory.utils.XPoint;
import com.google.gson.Gson;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemsFragment
        extends Fragment
        implements ListSubscriber {

    private static final String LIST_KEY = "LIST-KEY";

    static final String TAG = "XDBG.frg-items";

    private ListInfo li;

    private Gson gson = new Gson();

    private MainActivity mainActivity;

    public ItemsFragment() {
    }

    public static ItemsFragment newInstance(ListInfo li) {
        ItemsFragment fragment = new ItemsFragment();
        Bundle args = new Bundle();
        fragment.li = li;
        args.putString(LIST_KEY, fragment.li.key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String key = getArguments().getString(LIST_KEY);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        mainActivity.setAppHeader("Список: " + li.name);
        XPoint.getInstance().getStorage().subscribe(this);
    }

    @Override
    public void onStop(){
        super.onStop();
        XPoint.getInstance().getStorage().unsubscribe(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainActivity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fragment_items, container, false);
    }

    @Override
    public void onChange(String key, ListInfo li) {
        Log.d(TAG, gson.toJson(li));
    }

    @Override
    public String getKey() {
        return this.li.key;
    }
}
