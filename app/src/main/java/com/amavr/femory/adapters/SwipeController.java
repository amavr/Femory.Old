package com.amavr.femory.adapters;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;

import static androidx.recyclerview.widget.ItemTouchHelper.UP;
import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

public class SwipeController extends Callback {

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, LEFT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }
}
