package com.httpstwitter.birdfeedadmin;

/**
 * Created by Casey on 11/4/16.
 * Edited by Rebecca on 11/28/16.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.httpstwitter.birdfeedadmin.dataobjects.NaiveBayesKnowledgeBase;

import java.util.*;
import java.io.IOException;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;


public class Sentiment {

    FirebaseDatabase mdatabase;
    double rate, oldScore;
    Context context;
    String name;

    public Sentiment(String str, Context c, String n) throws IOException {
        rate = 0;
        name = n;
        context = c;
        rate += scoring(str);
        System.out.println(name+" "+rate);
        //oldScore();
    }

    public double scoring(String str) throws IOException {

        //map of dataset files
        Map<String, InputStream> trainingFiles = new HashMap<>();
        InputStream nis = null;
        InputStream pis = null;

        try {
            nis = context.getAssets().open("negative.txt");
            pis = context.getAssets().open("positive.txt");
        } catch(IOException ex) {
            ex.printStackTrace();
            System.out.println("Exception Caught: " + ex);
        }

        trainingFiles.put("Negative", nis);
        trainingFiles.put("Positive", pis);

        //loading examples in memory
        Map<String, String[]> trainingExamples = new HashMap<>();
        for(Map.Entry<String, InputStream> entry : trainingFiles.entrySet()) {
            trainingExamples.put(entry.getKey(), readLines(entry.getValue()));
        }

        //train classifier
        NaiveBayes nb = new NaiveBayes();
        nb.setChisquareCriticalValue(4.94); //0.01 pvalue   //originally set at 6.63
        nb.train(trainingExamples);

        //get trained classifier knowledgeBase
        NaiveBayesKnowledgeBase knowledgeBase = nb.getKnowledgeBase();

        nb = null;
        trainingExamples = null;

        //Use classifier
        nb = new NaiveBayes(knowledgeBase);
        Double sent = nb.predict(str);

        //System.out.println("Name: "+name+" Sentiment: " + sent);
        return sent;

    }


    /**
     * Reads the all lines from a file and places it a String array. In each
     * record in the String array we store a training example text.
     *
     *
     * @return
     * @throws IOException
     */
    @SuppressLint("NewApi")
    public static String[] readLines(InputStream is) throws IOException {

        List<String> lines = new ArrayList<>();
        String line = "";

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            if (is != null) {
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
        } finally {
            try { is.close(); } catch (Throwable ignore) {}
        }

        return lines.toArray(new String[lines.size()]);
    }

    public double getScore() {
        return rate;
    }

    public void oldScore() {
        FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
        DatabaseReference query = mdatabase.getReference("restaurants/"+name+"/score");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //System.out.println("Restaurant: "+name+" Old Score: "+dataSnapshot.getValue());
                rate += (double) dataSnapshot.getValue();
                System.out.println(name+" : "+rate);
                //System.out.println(name+" score: "+rate);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class getOldScore extends AsyncTask<Void, Void, ValueEventListener> {

        @Override
        protected ValueEventListener doInBackground(Void... voids) {
            ValueEventListener sent = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    rate += (double) dataSnapshot.getValue();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            //System.out.println(name+" Score: "+sent);
            return sent;
        }

        @Override
        protected void onPostExecute(ValueEventListener jpg) {
            DatabaseReference picture = mdatabase.getReference("restaurants/"+name+"/score");
            picture.addListenerForSingleValueEvent(jpg);
        }
    }

}
