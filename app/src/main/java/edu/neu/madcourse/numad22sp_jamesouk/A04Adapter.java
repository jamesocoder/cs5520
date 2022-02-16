package edu.neu.madcourse.numad22sp_jamesouk;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class A04Adapter extends RecyclerView.Adapter<A04Holder> {

    private final ArrayList<A04Card> itemList;
    private A04Card listener;

    public A04Adapter(ArrayList<A04Card> itemList) { this.itemList = itemList; }

    public void setOnItemClickListener(A04Card listener) { this.listener = listener; }

    @Override
    public A04Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_a04, parent, false);
        return new A04Holder(view, listener);
    }

    @Override
    public void onBindViewHolder(A04Holder holder, int position) {
        A04Card currentItem = itemList.get(position);

        holder.title.setText(currentItem.getTitle());
        holder.url.setText(currentItem.getUrl());
    }

    @Override
    public int getItemCount() { return itemList.size(); }
}
