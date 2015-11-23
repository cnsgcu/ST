package home.playground.services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import home.playground.models.TweetAnalytics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by cuong on 11/22/15.
 */
public class SentimentService
{
    public void sentimentAnalysis(TweetAnalytics tweet)
    {
        try {
            tweet.setSentiment(sentimentAnalysis(tweet.getText()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sentimentAnalysis(String text) throws IOException {
        final String apiKey = "2d1d83e324ca89a09731e06449b33073b2e5b48b";
        final String encodedText = URLEncoder.encode(text, "ISO-8859-1");

        final String sentimentApi = "http://access.alchemyapi.com/calls/text/TextGetTextSentiment?apikey=$apiKey&text=$encodedText&outputMode=json";

        URL url = new URL(sentimentApi);

        URLConnection conn = url.openConnection();
        final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuffer jsonBuff = new StringBuffer();
        String line = rd.readLine();

        do {
            jsonBuff.append(line);
            line = rd.readLine();
        } while(line != null);

        Gson gson = new Gson();
        JsonObject doc = gson.fromJson(jsonBuff.toString(), JsonElement.class).getAsJsonObject();

        return doc.get("docSentiment").getAsJsonObject().get("type").getAsString();
    }
}
