package edu.neu.madcourse.numad22sp_jamesouk;

import android.widget.Toast;

public class A04Card {

    // Each card will display the name of the website and its URL underneath it
    private final String title;
    private final String url;

    public A04Card(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() { return title; }
    public String getUrl() { return url; }

    // Launch the URL using a default browser
    public void onItemClick(int position) {
        // TODO: implement this method
    }
}
