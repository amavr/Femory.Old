package com.amavr.femory;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amavr.femory.adapters.ChangedTextCallback;
import com.amavr.femory.adapters.ListsAdapter;
import com.amavr.femory.adapters.SwipeController;
import com.amavr.femory.utils.Tools;
import com.amavr.femory.utils.XPoint;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListsFragment extends Fragment {

    static final String TAG = "XDBG.frg-lists";

    private RecyclerView rvLists;
    private ListsAdapter adp;
    private MainActivity mainActivity;

    public ListsFragment() {
        // Required empty public constructor
        Log.d(TAG, "constructor");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListsFragment newInstance() {
        ListsFragment fragment = new ListsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Context context = getContext();

        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_lists, container, false);

        rvLists = (RecyclerView)v.findViewById(R.id.rvLists);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        rvLists.setLayoutManager(manager);
        mainActivity = (MainActivity) getActivity();
        adp = new ListsAdapter(mainActivity); /// getActivity() - MainActivity

        rvLists.setAdapter(adp);

        FloatingActionButton fab = (FloatingActionButton)v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tools.dlgShowNewList(context, "Название списка", "Новый", new ChangedTextCallback() {
                    @Override
                    public void onChange(String text) {
                        if(text == null || text.length() == 0){
                            Tools.toastIt(context, "Имя не должно быть пустым");
                        }
                        else {
                            adp.createList(text);
                        }
                    }
                });
            }
        });

        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart");
        mainActivity.setAppHeader("Cписки");

//        HashMap<String, ListInfo> dic = DataEmitter.getLists(20);
//        adp.setLists(dic);

        XPoint.getInstance().getStorage().subscribe(adp);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        XPoint.getInstance().getStorage().unsubscribe(adp);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.lists_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "onCreateOptionsMenu");
    }


}
