package home.playground.services

import com.google.gson.Gson
import com.google.gson.JsonElement
import home.playground.models.Tweet
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder

/**
 * Created by cuong on 11/14/15.
 */
class SentimentAnalysis
{
    fun sentimentAnalysis(tweet: Tweet)
    {
        val apiKey = "243a800177544c32fa9c6dbcbdca4307d9dc9fe0"
        val text = URLEncoder.encode("thanksgiving is a good way to get along with family", "ISO-8859-1")

        val sentimentApi = "http://access.alchemyapi.com/calls/text/TextGetTextSentiment?apikey=$apiKey&text=$text&outputMode=json"

        val url = URL(sentimentApi)

        val conn = url.openConnection()
        val rd = BufferedReader(InputStreamReader(conn.inputStream));

        val jsonBuff = StringBuffer()
        var line: String? = rd.readLine()

        do {
            jsonBuff.append(line)
            line = rd.readLine()
        } while(line != null)

        val gson = Gson()
        val doc = gson.fromJson(jsonBuff.toString(), JsonElement::class.java).asJsonObject

        // TODO sometime get null pointer here
        tweet.sentiment = doc.get("docSentiment").asJsonObject.get("type").asString
    }
}