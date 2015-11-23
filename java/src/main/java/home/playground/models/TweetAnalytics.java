package home.playground.models;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by cuong on 11/22/15.
 */
public class TweetAnalytics implements Serializable
{
    private String text;
    private Integer age;
    private String gender;
    private String hashTag;
    private String mediaType;
    private String sentiment;
    private String profileImageUrl;
    private Timestamp createdDate;

    public static String resolveAgeGroup(Integer age)
    {
        if (age < 18) {
            return "child";
        } else if (age <= 24) {
            return "adult18to24";
        } else if (age <= 34) {
            return "adult25to34";
        } else if (age <= 44) {
            return "adult35to44";
        } else if (age <= 54) {
            return "adult45to54";
        } else if (age <= 64) {
            return "adult55to64";
        } else {
            return "adultOver64";
        }
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age)
    {
        this.age = age;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public String getHashTag()
    {
        return hashTag;
    }

    public void setHashTag(String hashTag)
    {
        this.hashTag = hashTag;
    }

    public String getMediaType()
    {
        return mediaType;
    }

    public void setMediaType(String mediaType)
    {
        this.mediaType = mediaType;
    }

    public String getSentiment()
    {
        return sentiment;
    }

    public void setSentiment(String sentiment)
    {
        this.sentiment = sentiment;
    }

    public String getProfileImageUrl()
    {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl)
    {
        this.profileImageUrl = profileImageUrl;
    }

    public Timestamp getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate)
    {
        this.createdDate = createdDate;
    }
}
