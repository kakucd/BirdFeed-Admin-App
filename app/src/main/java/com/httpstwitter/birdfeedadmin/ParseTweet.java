package com.httpstwitter.birdfeedadmin;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Scanner;

/**
 * Created by Casey on 4/18/17.
 */

public class ParseTweet {
    String tweet;
    String handle;
    String name;

    public ParseTweet() {
        tweet = "";
        handle = "";
        name = "";
    }

    public ParseTweet(String t) {
        tweet = t;
    }

    public String findHandle() {
        String temp;
        Scanner text = new Scanner(tweet);

        while(text.hasNext()) {
            temp = text.next();
            System.out.println("Temp: "+temp);

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = db.getReference();

            final String finalTemp = temp;
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(finalTemp)) {
                        handle = finalTemp;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        return handle;
    }
}
