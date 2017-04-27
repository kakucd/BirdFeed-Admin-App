package com.httpstwitter.birdfeedadmin;

import com.google.common.io.Files;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.serializer.KryoSerializer;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;

import twitter4j.auth.Authorization;
import twitter4j.auth.AuthorizationFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.charset.Charset;

public class StreamTweets {

    public StreamTweets() throws Exception {
        stream();
    }

    public void stream() throws Exception {
        // Twitter4J
        Configuration twitterConf = ConfigurationContext.getInstance();
        Authorization twitterAuth = AuthorizationFactory.getInstance(twitterConf);

        ObjectMapper mapper = new ObjectMapper();

        // Set up SparkConf
        SparkConf sparkConf = new SparkConf()
                .setAppName("Tweets Android")
                .setMaster("local[2]")
                .set("spark.serializer", KryoSerializer.class.getName())
                .set("es.nodes", "localhost:9200")
                .set("es.index.auto.create", "true");
        JavaStreamingContext sc = new JavaStreamingContext(sparkConf, new Duration(5000));

        //DataFrame for old tweets
        JavaSparkContext jsc = sc.sparkContext();
        final SQLContext[] sqlContext = {new SQLContext(jsc)};



        // output file, save streamed twitter data as a json file
        final File outputFile = new File("restaurant.json");

        //create a DStream of tweets
        String[] filters = { "@mattsinthemkt", "@IvarsClam", "@pikeplchowder", "@BaccoCafeSea", "@RadiatorWhiskey",
                "@CuttersCrab", "@japonessa", "@PinkDoorSeattle", "@CrepedeFrance1", "@place_pigalle",
                "@canlis", "@MetGrill", "@thewalrusbar", "@thecrumpetshop_", "@PiroshkyBakery",
                "@dahlialounge", "@RN74Seattle", "@ElGauchoSteak", "@Spinasse", "@ILBistroSeattle",
                "@PalisadeSea", "@SaltySeattle", "@Andaluca", "@RockCreekSea", "@salareseattle",
                "@AOTTSeattle", "@AlturaSeattle", "@GoldfinchTavern", "@raysboathouse", "@SeriousPieDT",
                "@SeriousPiePike", "@ilcorvopasta", "@ElliottsSeattle", "@mamnoontoo", "@DelanceySeattle",
                "@WildGingerEats", "@TheCarlileRoom", "@NellsRestaurant", "@steelheaddiner", "@EatStoneburner",
                "@SandPointGrill1", "@LOWELLSBar", "@ChandlersCrab", "@Local360Seattle", "@curb_cuisine",
                "@DukesChowder", "@PiattiSeattle", "@petitcochonsea", "@brimmerheeltap", "@rione_xiii",
                "@BrunswickHunt", "@coastalkitchen", "@bramlingballard", "@13CoinsSeattle", "@blueacreseafood",
                "@CafeMunir", "@SeatownSeabar", "@oldstovebeer", "@pikebrewing", "@SeaCoffeeWorks",
                "@BeechersSeattle", "@CanonSeattle", "@TheVirginiaInn", "@biscuitbitch", "@NoBonesTruck",
                "@LePanierBakery", "@Chanseattle"};


        //get twitter stream as a string with only english tweets and containing filters
        JavaDStream<String> stream = TwitterUtils.createStream(sc, twitterAuth, filters)
                .map(s -> new Tweet(s.getUser().getName(), s.getText(), s.getCreatedAt(), s.getPlace(), s.getGeoLocation(), s.getLang(), null))
                .map(t -> mapper.writeValueAsString(t))
                .filter(t -> t.contains("\"language\":\"en\""));


        Files.append("[ ", outputFile, Charset.defaultCharset());

        // save stream to json file
        stream.foreachRDD(tweets -> {
            tweets.collect().stream().forEach(t -> System.out.println(t));
            tweets.foreach(t -> {
                Files.append(t + ",\n", outputFile, Charset.defaultCharset());
            });
            return null;
        });

        sc.start();
//        sc.awaitTermination();
        sc.awaitTermination(500000);
        sc.stop();

        Files.append("{} ]", outputFile, Charset.defaultCharset());

    }

}


