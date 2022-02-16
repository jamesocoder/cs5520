package edu.neu.madcourse.numad22sp_jamesouk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class A04 extends AppCompatActivity {

    private ArrayList<A04Card> itemList = new ArrayList<>();
    private RecyclerView rview;
    private A04Adapter adapter;
    private RecyclerView.LayoutManager rLayoutMngr;
    private FloatingActionButton fab;

    private static final String ITEM_COUNT = "ITEM_COUNT";
    private static final String ATTR = "ATTRIBUTE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a04);

        init(savedInstanceState);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = 0;
                addItem(pos);
                // TODO: Add link adder
                Toast.makeText(getApplicationContext(), "Add Link", Toast.LENGTH_SHORT).show();
            }
        });

        // ItemTouchHelper doesn't seem to have a click+hold interaction, so I'm purposing a right-swipe to mean the user wants to edit a link
        ItemTouchHelper tHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView rView, RecyclerView.ViewHolder rHldr, RecyclerView.ViewHolder target) { return false; }

            @Override
            public void onSwiped(RecyclerView.ViewHolder rHldr, int direction) {
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        // TODO: Add Snackbar for delete
                        Toast.makeText(getApplicationContext(), "Left", Toast.LENGTH_SHORT).show();
                        int pos = rHldr.getLayoutPosition();
                        itemList.remove(pos);
                        adapter.notifyItemRemoved(pos);
                        break;
                    case ItemTouchHelper.RIGHT:
                        // TODO: Add edit function
                        // BUG: Both right and left swipes are leading to card deletion.  This switch is doing nothing?
                        Toast.makeText(getApplicationContext(), "Right", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        tHelper.attachToRecyclerView(rview);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int size = itemList == null ? 0 : itemList.size();
        outState.putInt(ITEM_COUNT, size);

        for (int i=0; i<size; i++) {
            outState.putString(i + ATTR + "0", itemList.get(i).getTitle());
            outState.putString(i + ATTR + "1", itemList.get(i).getUrl());
        }
        super.onSaveInstanceState(outState);
    }

    private void init(Bundle savedInstanceState) {
        initialItemData(savedInstanceState);
        createRecyclerView();
    }

    private void initialItemData(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(ITEM_COUNT)) {
            if (itemList == null || itemList.size() == 0) {
                int size = savedInstanceState.getInt(ITEM_COUNT);

                for (int i=0; i<size; i++) {
                    String title = savedInstanceState.getString(i + ATTR + "0");
                    String url = savedInstanceState.getString(i + ATTR + "1");
                    A04Card card = new A04Card(title, url);
                    itemList.add(card);
                }
            }
        }
    }

    private void createRecyclerView() {
        rLayoutMngr = new LinearLayoutManager(this);
        rview = findViewById(R.id.rView);
        rview.setHasFixedSize(true);
        adapter = new A04Adapter(itemList);
        A04Card itemClickListener = new A04Card() {
            @Override
            public void onItemClick(int position) {
                itemList.get(position).onItemClick(position);
                adapter.notifyItemChanged(position);
            }
        };
        adapter.setOnItemClickListener(itemClickListener);
        rview.setAdapter(adapter);
        rview.setLayoutManager(rLayoutMngr);
    }

    private void addItem(int position) {
        itemList.add(position, new A04Card("New Title", "New URL"));
        // TODO: add snackbar notification for added item
        adapter.notifyItemInserted(position);
    }
}