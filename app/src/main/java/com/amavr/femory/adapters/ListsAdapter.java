package com.amavr.femory.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.amavr.femory.ItemsFragment;
import com.amavr.femory.MainActivity;
import com.amavr.femory.R;
import com.amavr.femory.models.ListInfo;
import com.amavr.femory.utils.Tools;
import com.amavr.femory.utils.XPoint;
import com.amavr.femory.utils.XStorage;

import java.util.ArrayList;
import java.util.List;

public class ListsAdapter
        extends RecyclerView.Adapter<ListsAdapter.ListHolder>
        implements ListSubscriber {

    static final String TAG = "XDBG.adp";

    List<ListInfo> lists = new ArrayList<>();
    List<String> keys = new ArrayList<>();

    RecyclerView rvList;
    MainActivity main_activity; /// MainActivity

    public ListsAdapter(MainActivity activity){
        this.main_activity = activity;
        lists = XPoint.getInstance().getLists();
    }

    public ListInfo createList(String name){
        ListInfo li = XPoint.getInstance().getStorage().addNewList(name);
        if(li != null) {
            lists.add(li);
        }
        return li;
    }

    @NonNull
    @Override
    public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        final View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.lists_item, parent, false);

        rvList = (RecyclerView) parent;

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = rvList.getChildLayoutPosition(view);
                ListInfo li = lists.get(position);
                String item = li.name;
                Toast.makeText(view.getContext(), item, Toast.LENGTH_LONG).show();

                Fragment newFragment = ItemsFragment.newInstance(li);
                FragmentTransaction transaction = main_activity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.flContent, newFragment);
                transaction.addToBackStack(null);

//// Commit the transaction
                transaction.commit();
            }
        });
        return new ListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        holder.bind(this.lists.get(position));
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, String.format("getItemCount: %s", this.lists.size()));
        return this.lists.size();
    }

    @Override
    public void onChange(String key, ListInfo li) {
        Log.d(TAG, String.format("onChange, key: %s", key));
        boolean found = false;
        for(int i = lists.size() - 1; i >= 0; i--){
            if(lists.get(i).key.equals(key)){
                found = true;
                if(li == null){
                    lists.remove(i);
                }
                else {
                    lists.set(i, li);
                }
                break;
            }
        }
        if(!found){
            lists.add(li);
        }
        notifyDataSetChanged();
    }

    @Override
    public String getKey() {
        /// интересуют обновления всех списков, поэтому - null
        return null;
    }

    class ListHolder extends RecyclerView.ViewHolder {

        View view;

        ListInfo li;
        TextView tvName;
        ImageView btnMenu;

        public ListHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "ListHolder");
            view = itemView;

            tvName = (TextView)view.findViewById(R.id.tvName);
            btnMenu = (ImageView)view.findViewById(R.id.btnItemMenu);
            btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(v);
                }
            });
        }

        public void bind(ListInfo li){
            Log.d(TAG, "ListHolder.bind");
            this.li = li;
            tvName.setText(this.li.name);
        }

        private void showPopupMenu(View v) {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.inflate(R.menu.lists_menu);

            popupMenu
                    .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_delete:
                                    deleteList(view);
                                    return true;
                                case R.id.action_rename:
                                    Toast.makeText(view.getContext(),
                                            "Вы выбрали RENAME",
                                            Toast.LENGTH_SHORT).show();
                                    return renameList(view);
                                case R.id.action_share:
                                    Toast.makeText(view.getContext(),
                                            "Вы выбрали SHARE",
                                            Toast.LENGTH_SHORT).show();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });

            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu menu) {
                    Toast.makeText(view.getContext(), "onDismiss",
                            Toast.LENGTH_SHORT).show();
                }
            });
            popupMenu.show();
        }

        private void deleteList(View v){
            final Context context = v.getContext();

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Внимание")
                    .setMessage("Вы хотите удалить список "+ li.name +"?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Tools.toastIt(context, "OK");
                            XPoint.getInstance().getStorage().delList(li.key);
                            onChange(li.key, null);
                        }})
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }

        private boolean renameList(View v){
            final Context context = v.getContext();
            final String old_name = tvName.getText().toString();
            Tools.dlgShowNewList(context, "Название", old_name, new ChangedTextCallback() {
                @Override
                public void onChange(String text) {
                    if(text == null || text.length() == 0){
                        Tools.toastIt(context, "Имя не должно быть пустым");
                    }
                    else if(text.equals(old_name)){
                        Tools.toastIt(context, "Нет изменений");
                    }
                    else {
                        XStorage x = XPoint.getInstance().getStorage();
                        tvName.setText(text);
                        li.name = text;
                        x.updList(li);
                    }
                }
            });

            return true;
        }

    }

}
