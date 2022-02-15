package edu.neu.madcourse.numad22sp_jamesouk;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class A04Holder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView url;

    public A04Holder(View itemView, final A04Card listener) {
        super(itemView);
        title = itemView.findViewById(R.id.card_title);
        url = itemView.findViewById(R.id.card_url);

        // This registers a card's listener, causing it to perform the action defined in A04Card.java
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int pos = getLayoutPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onItemClick(pos);
                    }
                }
            }
        });
    }
}
