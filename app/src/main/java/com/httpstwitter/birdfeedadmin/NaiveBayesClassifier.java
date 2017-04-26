package com.httpstwitter.birdfeedadmin;

import android.annotation.SuppressLint;

import com.httpstwitter.birdfeedadmin.dataobjects.NaiveBayesKnowledgeBase;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Rebecca on 2/24/17.
 */
public class NaiveBayesClassifier {
    /**
     * Main method
     *
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        //map of dataset files
        Map<String, File> trainingFiles = new HashMap<>();
        trainingFiles.put("Negative", new File("negative.txt"));
        trainingFiles.put("Positive", new File("positive.txt"));

        //loading examples in memory
        Map<String, String[]> trainingExamples = new HashMap<>();
        for(Map.Entry<String, File> entry : trainingFiles.entrySet()) {
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
        String example1 = "This is delicious!";
        Double output1 = nb.predict(example1.toLowerCase().replaceAll("[^a-zA-Z\\s]","").replaceAll("\\s+"," "));
        System.out.format("The sentence \"%s\" was classified as \"%s\".%n", example1, output1);

        String example2 = "This is so gross. Tastes like dirty socks. Disgusting";
        Double output2 = nb.predict(example2.toLowerCase().replaceAll("[^a-zA-Z\\s]","").replaceAll("\\s+"," "));
        System.out.format("The sentence \"%s\" was classified as \"%s\".%n", example2, output2);

        String example3 = "I hated my meal and the service was terrible.";
        Double output3 = nb.predict(example3.toLowerCase().replaceAll("[^a-zA-Z\\s]","").replaceAll("\\s+"," "));
        System.out.format("The sentence \"%s\" was classified as \"%s\".%n", example3, output3);

        String example4 = "Ew";
        Double output4 = nb.predict(example4.toLowerCase().replaceAll("[^a-zA-Z\\s]","").replaceAll("\\s+"," "));
        System.out.format("The sentence \"%s\" was classified as \"%s\".%n", example4, output4);

        String example5 = "This is the spot to go to for authentic Ethiopian food. The chicken is so moist and tender.";
        Double output5 = nb.predict(example5.toLowerCase().replaceAll("[^a-zA-Z\\s]","").replaceAll("\\s+"," "));
        System.out.format("The sentence \"%s\" was classified as \"%s\".%n", example5, output5);

        String example6 = "This is not good";
        Double output6 = nb.predict(example6.toLowerCase().replaceAll("[^a-zA-Z\\s]","").replaceAll("\\s+"," "));
        System.out.format("The sentence \"%s\" was classified as \"%s\".%n", example6, output6);

        String example7 = "This is not bad";
        Double output7 = nb.predict(example7.toLowerCase().replaceAll("[^a-zA-Z\\s]","").replaceAll("\\s+"," "));
        System.out.format("The sentence \"%s\" was classified as \"%s\".%n", example7, output7);

    }

    /**
     * Reads the all lines from a file and places it a String array. In each
     * record in the String array we store a training example text.
     *
     * @param file
     * @return
     * @throws IOException
     */
    @SuppressLint("NewApi")
    public static String[] readLines(File file) throws IOException {

//        Scanner fileReader = new Scanner(file);
        List<String> lines;
        try (Scanner reader = new Scanner(file)) {
            lines = new ArrayList<>();
            String line = reader.nextLine();
            while (reader.hasNextLine()) {
                lines.add(line);
                line = reader.nextLine();
            }
        }
        return lines.toArray(new String[lines.size()]);
    }

}
