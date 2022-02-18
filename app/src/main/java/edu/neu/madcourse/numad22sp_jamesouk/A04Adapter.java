package edu.neu.madcourse.numad22sp_jamesouk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class A04Adapter extends RecyclerView.Adapter<A04Adapter.A04ViewHolder> {

    List<A04Item> itemList;
    Context context;

    public A04Adapter(List<A04Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    // This subclass is used to contain references to the Views we defined in our Layout for Items
    public static class A04ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView url;
        ConstraintLayout parentLayout;

        public A04ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.card_title);
            url = itemView.findViewById(R.id.card_url);
            parentLayout = itemView.findViewById(R.id.card_constraint_layout);
        }
    }

    // The 3 methods below are required methods of the RecyclerView.Adapter class
    @NonNull
    @Override
    // This method returns the subclass defined above
    public A04ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /* First we inflate a layout.  A LayoutInflater expands our layout, making the Views in the
        layout referencable.

        .from identifies which Context to look for this layout in.

        The arguments to .inflate are:
            int: The id of the Layout for Items we defined
            android.view.ViewGroup: Typically parent. This references the lowest namespace of the app,
                where all layouts can be found
            attachToRoot: Typically false
         */
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.a04_layout_item, parent, false);

        // This line links the overarching View containing the Views inside our Layout of Items to
        // our subclass, making the Views in the layout available to be found by the subclass
        A04ViewHolder holder = new A04ViewHolder(view);

        return holder;
    }

    @Override
    /* This method pulls the data out of an Item from within our Item List and puts it into the Views
       declared in our subclass.  Recall that the Item List is obtained when we instantiate our
       Adapter class.  In this way, the data in our List is now displayed in our Layout with multiple
       instances of the ViewHolder subclass containing each individual Item's data.

       Caution: If your Item contains a numerical value and you are trying to display it in a TextView,
       you need to cast the number returned by the Item's getter as a String.  You could use
       String.valueOf(...Item.getNumber()) to do this.
    */
    public void onBindViewHolder(@NonNull A04ViewHolder holder, int position) {
        holder.title.setText(itemList.get(position).getTitle());
        holder.url.setText(itemList.get(position).getUrl());
        // Listen for clicks on the container representing each Item (in this case, a ConstraintLayout)
        // Open a URL when an Item is clicked
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri webpage = Uri.parse(itemList.get(holder.getAdapterPosition()).getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}