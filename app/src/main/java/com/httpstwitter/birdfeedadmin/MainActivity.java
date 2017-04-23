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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ArrayList<Tweet> data = new ArrayList<>();
    ArrayList<Handle> handles = new ArrayList<>();
    String name;

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
    public void onStart() {
        super.onStart();
        //System.out.println("Name!!: "+name);
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

    /*
     * Will run sentiment analysis on data that is stored in the designated JSON file.
     * snackbar message should indicate either when sentiment is successfully competed or when the
     * function has failed.
     */
    public void sentiment(View view) {
        boolean flag;
        try {
            loadJSONFromAsset();
            flag = true;
            //System.out.println("loadJSONFromAsset");
        } catch (JSONException e) {
            e.printStackTrace();
            flag = false;
        }

        String message;
        if(flag)
           message = "data successfully read";
        else
            message = "data unsuccessfully read";
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void loadJSONFromAsset() throws JSONException {
        InputStream is = null;
        try {
            is = getAssets().open("restaurant.json");
            //System.out.println("inputstream");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //System.out.println("json String initialized");
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            //System.out.println("Exception caught");
            e.printStackTrace();
        }

        try {
            assert reader != null;
            reader.beginObject();
            reader.nextName();
            reader.beginArray();
            while (reader.hasNext() && reader.peek() != JsonToken.END_DOCUMENT) {
                //System.out.println(reader.peek());
                readTweet(reader);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readTweet(JsonReader reader) throws IOException {
        reader.beginObject();
        //reader.nextName();
        String text = null;
        String user = null;
        String date = null;
        while (reader.hasNext()) {
            String name;
            try {
                name = reader.nextName();
                //System.out.println("Name: "+ name);
            } catch(Exception e) {
                //System.out.println("Exception caught: "+e.toString());
                break;
            }
            if (name.equals("user")) {
                user = reader.nextString();
                //System.out.println("User: " + user);
            } else if (name.equals("text")) {
                text = reader.nextString();
                //System.out.println("Text: " + text);
            } else if (name.equals("createdAt")) {
                date = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        //System.out.println("User: " + user);
        //System.out.println("Text: " + text);
        reader.endObject();
        handles.add(new Handle(user, text, date));
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

        public String getTweet() {
            return tweet;
        }

        public String getUser() {
            return user;
        }
    }

    /*
     * Method updated the database by pushing data that has been stream and analyzed for sentiment.
     * This method updates both tweet information in the database and sentiment scores.
     * snackbar message should indicate when function in successfully completed or when function
     * has failed.
     */
    public void push(View view) {
        for(int i = 0; i < handles.size(); i++) {
            Handle h = handles.get(i);
            if (h.getName() == null) {
                handles.remove(i);
                i--;
            }
        }

        FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();

        for(int i = 0; i < handles.size(); i++) {
            Handle t = handles.get(i);
            final String text = t.getTweet();
            final String user = t.getUser();
            final String restaurant = t.getName();
            final double score = t.getScore();
            final String date = t.getDate();


            final DatabaseReference ref = mdatabase.getReference("tweets/" + restaurant + "/" + date);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ref.setValue(text);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        Snackbar.make(view, "Database Successfully Updated!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    /*
     * Will stream data from Twitter when corresponding button is clicked.
     * snackbar message should indicate when the streaming function has either failed or
     * successfully completed.
     */
    public void stream(View view) {

        Snackbar.make(view, "No Action Set", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
