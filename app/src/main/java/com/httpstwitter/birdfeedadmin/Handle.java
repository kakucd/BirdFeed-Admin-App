package com.httpstwitter.birdfeedadmin;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Scanner;
/**
 * Created by Casey on 4/22/17.
 */

public class Handle {
    String tweet, name, handle, user, date;
    double score;

    public Handle() {
        tweet = null;
        name = null;
        handle = null;
        user = null;
        date = null;
        score = 0;
    }

    public Handle(String u, String t, String d) {
        tweet = t;
        user = EncodeString(u);
        date = d;
        name = parseTweet();
    }

    public String parseTweet() {
        Scanner text = new Scanner(EncodeString(tweet));
        String temp;

        while(text.hasNext()) {
            temp = text.next();
            //System.out.println("Temp: "+temp);

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = db.getReference("handle/");

            final String finalTemp = temp;
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(finalTemp)) {
                        handle = finalTemp;
                        //System.out.println("If: "+finalTemp);
                        name = getRestaurant(handle);
                    } else {
                        //System.out.println("Else: "+finalTemp);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        System.out.println("this.name: "+this.name);
        return this.name;
    }

    public static String EncodeString(String string) {
        string = string.replace("#", ",");
        string = string.replace("$", ",");
        string = string.replace(".", ",");
        string = string.replace("[", ",");
        string = string.replace("]", ",");
        return string;
    }

    public String getRestaurant(String handle) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("handle/"+handle);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = (String) dataSnapshot.getValue();
                //System.out.println("name: "+name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                name = null;
            }
        });
        return name;
    }

    public String getTweet() {
        return tweet;
    }

    public String getName() {
        return name;
    }

    public String getHandle() {
        return handle;
    }

    public void setScore(double s) {
        score = s;
    }

    public double getScore() {
        return score;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String u) {
        user = u;
    }

    public void setTweet(String t) {
        tweet = t;
    }

    public String getDate() {
        return date;
    }
}
