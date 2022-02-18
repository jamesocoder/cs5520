package edu.neu.madcourse.numad22sp_jamesouk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class A04Main extends AppCompatActivity {

    // Constants for our Bundle object for saving an App's state
    private static final String ICNT = "ITEM_COUNT";
    private static final String ATTR = "ATTRIBUTE";

    private FloatingActionButton fab_add;
    private List<A04Item> itemList = new ArrayList<A04Item>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a04_activity_main);

        /* Set the FloatingActionButton to launch the Activity where we gather input for a new Item
           It uses deprecated functions that should be replaced with the ones defined here:
           https://developer.android.com/training/basics/intents/result#launch
         */
        fab_add = findViewById(R.id.a04_btn_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(A04Main.this, A04Add.class);
                /* Call Intent.setFlags() to add:
                    1: i.getFlags() - The flags Intents start with by default
                    2: ...HISTORY - A flag that does not add the new Activity to the backstack
                   These 2 arguments are passed in together by combining them with '|'
                 */
                i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivityForResult(i, 1);
            }
        });

        // Reinitialize any items left from the last session into our new List of Items
        unpack(savedInstanceState);

        // Set a reference to the RecyclerView so we have access to its setters
        recyclerView = findViewById(R.id.a04_view_recycler);

        // This setting improves performance when we know that the view's size will not change to
        // fit the items in it when they are removed or added; it will stay constant
        recyclerView.setHasFixedSize(true);

        // Instantiate a layout manager that will handle organizing the items within the RecyclerView
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);

        // Add Items to test with so we don't have to add them every time we restart the app
        itemList.add(new A04Item("YouTube", "https://www.youtube.com"));
        itemList.add(new A04Item("Google", "https://google"));
        itemList.add(new A04Item("Reddit", "https://www.reddit.com"));

        /* The Adapter is a class we must define to link the data contained within our List of Items
           to the Views in the Layout for Items we created.  Once we instantiate this adapter, any
           Item that was in our List will now be given a specific position on the RecyclerView.
           These position values must be updated as our List is updated.
         */
        recyclerAdapter = new A04Adapter(itemList, A04Main.this);
        recyclerView.setAdapter(recyclerAdapter);

        /* Set up an ItemTouchHelper, a Class that helps an App understand a user's swipes and drags
           This helper is only set up to recognize left and right swipes.  It only interprets them
           as delete actions.  I was unable to get the code to differentiate between left and right
           for "delete" and "edit" actions, respectively.
         */
        ItemTouchHelper tHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView rView, RecyclerView.ViewHolder rHldr, RecyclerView.ViewHolder target) { return false; }

            @Override
            public void onSwiped(RecyclerView.ViewHolder rHldr, int direction) {
                int pos = rHldr.getLayoutPosition();
                itemList.remove(pos);
                recyclerAdapter.notifyItemRemoved(pos);
            }
        });
        tHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    // This method unpacks and handles the results returned from a sub-activity launched with
    // startActivityForResult(), the FloatingActionButton's response to a click
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* Presumably, super.onActivityResult() unregisters the result object from memory so it isn't
           confused with others.

           Operating a switch on the requestCode allows us to specify handling for different
           sub-activities; each activity, when launched should've been given a unique, categorical
           requestCode identifier.

           resultCode is either one of the RESULT_ values defined in Intent or a custom value defined
           by you.  You can define a custom result code if the given ones don't encompass all of your
           possible result cases.

           Next, although we confirm a RESULT_OK resultCode, we still check if the result data
           has the tags that we expect it to.  Only then will we finally add the Item to our List of
           Items.

           Finally, we must notify our RecyclerView.Adapter that its dataset has changed so it can
           properly update its position values.  Since we added an item to the end of our List, we
           can just pass in itemList.size()-1 to .notifyItemInserted() to indicate the inserted Item
           is in the last position.
         */
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    if (data.hasExtra("title") && data.hasExtra("url")) {
                        itemList.add(new A04Item(
                                data.getExtras().getString("title"),
                                data.getExtras().getString("url")
                        ));
                        recyclerAdapter.notifyItemInserted(itemList.size() - 1);

                        /* This codeblock presents a Snackbar to the user that allows them to open
                           the link they just added. A04Add handles validating their input before
                           this.
                         */
                        FloatingActionButton vfab = findViewById(R.id.a04_btn_add);
                        View.OnClickListener snackbarClickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Uri webpage = Uri.parse(itemList.get(itemList.size()-1).getUrl());
                                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                                startActivity(intent);
                            }
                        };
                        Snackbar.make(vfab, "Link Added", Snackbar.LENGTH_LONG)
                                .setAction("OPEN", snackbarClickListener).show();
                    }
                }
                break;
            default:
                return;
        }
    }

    @Override
    /* This method creates a Bundle of data which is a set of Key:Value pairs that we define to be
       passed on to the next instance of this App.  Of note, a new instance of an app is created
       every time a devices orientation changes when the app is still open.
     */
    protected void onSaveInstanceState(Bundle outState) {
        int size = itemList == null ? 0 : itemList.size();
        outState.putInt(ICNT, size);

        for (int i=0; i<size; i++) {
            outState.putString(i + ATTR + "0", itemList.get(i).getTitle());
            outState.putString(i + ATTR + "1", itemList.get(i).getUrl());
        }
        super.onSaveInstanceState(outState);
    }

    /* This method checks if the App is being reactivated.  A Bundle object passed in by the Android
       system will be non-null if so.  If there is a savedInstanceState, this method will reinstantiate
       all of the Items defined within.
     */
    private void unpack(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(ICNT)) {
            if (itemList == null || itemList.size() == 0) {
                int size = savedInstanceState.getInt(ICNT);

                for (int i = 0; i < size; i++) {
                    String title = savedInstanceState.getString(i + ATTR + "0");
                    String url = savedInstanceState.getString(i + ATTR + "1");
                    itemList.add(new A04Item(title, url));
                }
            }
        }
    }
}