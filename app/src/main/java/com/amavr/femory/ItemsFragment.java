package com.amavr.femory;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amavr.femory.adapters.ChangedTextCallback;
import com.amavr.femory.adapters.ItemsAdapter;
import com.amavr.femory.adapters.ListSubscriber;
import com.amavr.femory.adapters.SwipeController;
import com.amavr.femory.models.ListInfo;
import com.amavr.femory.utils.Tools;
import com.amavr.femory.utils.XPoint;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemsFragment extends Fragment {

    private static final String LIST_KEY = "LIST-KEY";

    static final String TAG = "XDBG.frg-items";

    private ListInfo li;

    private Gson gson = new Gson();

    private RecyclerView rvItems;
    private ItemsAdapter adp;
    private MainActivity mainActivity;

    public ItemsFragment(ListInfo li) {
        Log.d(TAG, "constructor");
        this.li = li;
    }

    public static ItemsFragment newInstance(ListInfo li) {
        ItemsFragment fragment = new ItemsFragment(li);
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
        XPoint.getInstance().getStorage().subscribe(adp);
        mainActivity.setAppHeader(li.name);
    }

    @Override
    public void onStop(){
        super.onStop();
        XPoint.getInstance().getStorage().unsubscribe(adp);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Context context = getContext();

        mainActivity = (MainActivity) getActivity();
        View v = inflater.inflate(R.layout.fragment_items, container, false);

        adp = new ItemsAdapter(mainActivity, li);

        rvItems = (RecyclerView)v.findViewById(R.id.rvItems);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        rvItems.setLayoutManager(manager);
        rvItems.setAdapter(adp);

        SwipeController swipeController = new SwipeController();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(rvItems);


        FloatingActionButton fab = (FloatingActionButton)v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tools.dlgShowNewList(context, "Название", "Новый", new ChangedTextCallback() {
                    @Override
                    public void onChange(String text) {
                        if(text == null || text.length() == 0){
                            Tools.toastIt(context, "Имя не должно быть пустым");
                        }
                        else {
                            adp.createItem(text);
                        }
                    }
                });
            }
        });

        return v;
    }

}
