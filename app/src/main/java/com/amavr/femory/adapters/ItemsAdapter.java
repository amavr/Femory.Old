package com.amavr.femory.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amavr.femory.MainActivity;
import com.amavr.femory.R;
import com.amavr.femory.models.ItemInfo;
import com.amavr.femory.models.ListInfo;
import com.amavr.femory.utils.XPoint;

public class ItemsAdapter
        extends RecyclerView.Adapter<ItemsAdapter.ItemHolder>
        implements ListSubscriber {

    static final String TAG = "XDBG.adp-items";

    ListInfo li;
    RecyclerView rvList;
    MainActivity main_activity; /// MainActivity

    public ItemsAdapter(MainActivity activity, ListInfo list){
        this.main_activity = activity;
        this.li = list;
    }

    public ItemInfo createItem(String name){
        ItemInfo  item = new ItemInfo();
        item.setName(name);
        item.setDone(false);
        if(li != null) {
            li.items.add(item);
        }

        XPoint.getInstance().getStorage().updListAtFB(li);
        return item;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.items_item, viewGroup, false);

        rvList = (RecyclerView) viewGroup;

        return new ItemsAdapter.ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.bind(this.li.items.get(position));
    }

    @Override
    public int getItemCount() {
        int num = this.li.items.size();
        Log.d(TAG, String.format("getItemCount: %s", num));
        return num;
    }

    @Override
    public void onChange(String key, ListInfo li) {
        Log.d(TAG, String.format("onChange, key: %s", key));

        if(key == this.li.key){
            this.li = li;
            main_activity.setAppHeader(li.name);
        }
        notifyDataSetChanged();
    }

    @Override
    public String getKey() {
        return li.key;
    }

    class ItemHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        View view;

        ItemInfo item;
        TextView tvName;
        ImageView btnDone;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "ItemHolder");
            view = itemView;
            tvName = (TextView) view.findViewById(R.id.tvName);
            btnDone = (ImageView) view.findViewById(R.id.btnDone);

            view.setOnClickListener(this);
            tvName.setOnClickListener(this);
            btnDone.setOnClickListener(this);
        }

        private void switchDone(){
            item.setDone(!item.isDone());
            setBtnDone(item.isDone());
            XPoint.getInstance().getStorage().updListAtFB(li);
            notifyDataSetChanged();
        }

        public void bind(ItemInfo item){
            Log.d(TAG, "ListHolder.bind");
            this.item = item;
            tvName.setText(this.item.getName());
            setBtnDone(item.isDone());
        }

        public void setBtnDone(boolean isDone){
            if(isDone) {
                btnDone.setImageResource(R.drawable.ic_check_black_24dp);
            }
            else{
                btnDone.setImageIcon(null);
            }
        }

        @Override
        public void onClick(View v) {
            switchDone();
        }
    }
}
