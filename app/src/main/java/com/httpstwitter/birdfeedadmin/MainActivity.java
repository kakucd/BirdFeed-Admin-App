package com.httpstwitter.birdfeedadmin;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.util.JsonReader;
import android.util.JsonToken;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayList<Tweet> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void push(View view) {
        try {
            loadJSONFromAsset();
            System.out.println("loadJSONFromAsset");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadJSONFromAsset() throws JSONException {
        InputStream is = null;
        try {
            is = getAssets().open("restaurant.json");
            System.out.println("inputstream");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println("json String initialized");
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.out.println("Exception caught");
            e.printStackTrace();
        }

        try {
            assert reader != null;
            reader.beginObject();
            reader.nextName();
            reader.beginArray();
            while (reader.hasNext() && reader.peek() != JsonToken.END_DOCUMENT) {
                System.out.println(reader.peek());
                data.add(readTweet(reader));
            }
        } catch (IOException e) {
            for(int i = 0; i < data.size(); i++) {
                System.out.println("data: "+data.get(i));
            }
            e.printStackTrace();
        }

    }

    public Tweet readTweet(JsonReader reader) throws IOException {
        String text = null;
        String user = null;

        reader.beginObject();
        reader.nextName();

        while (reader.hasNext()) {
            String name, temp;
            try {
                name = reader.nextString();
                System.out.println("Name: "+name);
            } catch(Exception e) {
                System.out.println("Exception caught: "+e.toString());
                break;
            }
            if (name.equals("user")) {
                user = reader.nextString();
                System.out.println("User: " + user);
            } else if (name.equals("text")) {
                text = reader.nextString();
                System.out.println("Text: " + text);
            } else {
                reader.skipValue();
                //reader.endObject();
            }
        }

        return new Tweet(user, text);
    }

    public class Tweet {
        String user;
        String tweet;

        public Tweet() {
            user = null;
            tweet = null;
        }

        public Tweet(String u, String t) {
            user = u;
            tweet = t;
        }
    }
}
