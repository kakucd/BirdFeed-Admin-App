package com.httpstwitter.birdfeedadmin;

/**
 * Created by Casey on 11/4/16.
 * Edited by Rebecca on 11/28/16.
 */

//https://www.tutorialspoint.com/spark_sql/spark_sql_useful_resources.htm
//https://spark.apache.org/docs/1.5.1/api/java/org/apache/spark/sql/DataFrame.html

import android.annotation.SuppressLint;

import com.httpstwitter.birdfeedadmin.dataobjects.NaiveBayesKnowledgeBase;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;


public class Sentiment {

    static HashMap<String, Integer> map = new HashMap<>();
    double rate;
    Context context;

    public Sentiment(String str, Context c) throws IOException {
        context = c;
        rate = scoring(str);
    }

    public double scoring(String str) throws IOException {

        //SparkConf sparkConf = new SparkConf()
          //      .setAppName("Tweets Android")
            //    .setMaster("local[2]");
        //SparkContext sc = new SparkContext(sparkConf);

        //SQLContext sqlContext = new SQLContext(sc);

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


        //try {
           /* DataFrame tweets = sqlContext.read().json("restaurant.json"); // load old tweets into a DataFrame
            tweets.registerTempTable("tweetDF");

            DataFrame tweetText = sqlContext.sql("SELECT text FROM tweetDF");
            long numTweets = tweetText.count();
            System.out.println(numTweets);

            //go through all tweets and analyze the sentiment of each
            for(int i = 0; i<numTweets; i++) {

                String tweet = tweetText.take((int) numTweets)[i].toString();
                tweet = tweet.substring(1, tweet.length() - 1);
                System.out.println(tweet);

                Double sent = nb.predict(str);

                System.out.println("Sentiment Prediction: " + sent);
                return sent;
            }

        } catch (Exception e){
            System.out.println(e);
        }*/
        Double sent = nb.predict(str);

        System.out.println("Sentiment Prediction: " + sent);
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

//        Scanner fileReader = new Scanner(file);
        List<String> lines = new ArrayList<>();
        String line = "";
        //try (Scanner reader = new Scanner(file)) {
          //  lines = new ArrayList<>();
            //String line = reader.nextLine();
            //while (reader.hasNextLine()) {
              //  lines.add(line);
                //line = reader.nextLine();
           // }
        //}

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

}
