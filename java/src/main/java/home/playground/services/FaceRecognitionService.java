package home.playground.services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import home.playground.models.FaceRecognition;
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
public class FaceRecognitionService
{
    private static final String apiKey = "49b985496341422f8251257fa06873ab";
    private static final String apiSecret = "Q64esshZDgV-o9h42WHStXgY8FF-N3z7";

    public void faceAnalysis(TweetAnalytics tweet)
    {
        try {
            final FaceRecognition fr = faceAnalysis(tweet.getProfileImageUrl().replace("normal", "400x400"));

            tweet.setAge(fr.getAge());
            tweet.setGender(fr.getGender());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public FaceRecognition faceAnalysis(String imgUrl) throws IOException, InterruptedException
    {
        final String encodeUrl = URLEncoder.encode(imgUrl, "ISO-8859-1");
        final FaceRecognition fr = new FaceRecognition();

        final StringBuffer jsonBuff = new StringBuffer();
        for (int retry = 1; retry <= 10; retry++) {
            final URL url = new URL("http://apius.faceplusplus.com/v2/detection/detect?api_key="
                    + apiKey + "&api_secret="
                    + apiSecret +"&url="
                    + encodeUrl + "&attribute=age%2Cgender%2Crace");

            final URLConnection conn = url.openConnection();

            try {
                final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = rd.readLine();
                do {
                    jsonBuff.append(line);
                    line = rd.readLine();
                } while(line != null);

                break;
            } catch(IOException e) {
                if (retry == 10) {
                    return fr;
                } else {
                    Thread.sleep(3000);
                }
            }
        }

        final Gson gson = new Gson();
        final JsonObject jsonObj = gson.fromJson(jsonBuff.toString(), JsonElement.class).getAsJsonObject();

        if (jsonObj.getAsJsonArray("face").size() != 0) {
            final JsonElement face = jsonObj.getAsJsonArray("face").get(0);

            fr.setAge(face.getAsJsonObject().get("attribute").getAsJsonObject().get("age").getAsJsonObject().get("value").getAsInt());
            fr.setRange(face.getAsJsonObject().get("attribute").getAsJsonObject().get("age").getAsJsonObject().get("range").getAsInt());
            fr.setGender(face.getAsJsonObject().get("attribute").getAsJsonObject().get("gender").getAsJsonObject().get("value").getAsString());
        }

        return fr;
    }
}
