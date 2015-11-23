package home.playground.models;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by cuong on 11/22/15.
 */
public class SentimentAnalytics implements Serializable
{
    private Integer total;
    private String sentiment;
    private Map<String, Map<String, Integer>> freq;

    public Integer getTotal()
    {
        return total;
    }

    public void setTotal(Integer total)
    {
        this.total = total;
    }

    public String getSentiment()
    {
        return sentiment;
    }

    public void setSentiment(String sentiment)
    {
        this.sentiment = sentiment;
    }

    public Map<String, Map<String, Integer>> getFreq()
    {
        return freq;
    }

    public void setFreq(Map<String, Map<String, Integer>> freq)
    {
        this.freq = freq;
    }
}
