package com.lorvent.project.telgujukebox;


import com.lorvent.project.telgujukebox.adapters.List1Adapter;
import com.lorvent.project.telgujukebox.adapters.List2Adapter;
import com.lorvent.project.telgujukebox.adapters.ListAdapter;

public interface RecyclerClickListener {

    void onItemClicked(ListAdapter.SimpleViewHolder holder, int position);
    void onItemClicked(List1Adapter.SimpleViewHolder holder, int position);
    void onItemClicked(List2Adapter.SimpleViewHolder holder, int position);

}
