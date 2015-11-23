package home.playground.models;

import java.io.Serializable;

/**
 * Created by cuong on 11/22/15.
 */
public class HashTagAnalytics implements Serializable
{
    private Integer count;
    private String hashTag;

    public Integer getCount()
    {
        return count;
    }

    public void setCount(Integer count)
    {
        this.count = count;
    }

    public String getHashTag()
    {
        return hashTag;
    }

    public void setHashTag(String hashTag)
    {
        this.hashTag = hashTag;
    }
}
