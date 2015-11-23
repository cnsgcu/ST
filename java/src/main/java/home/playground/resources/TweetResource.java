package home.playground.resources;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import home.playground.models.SentimentAnalytics;
import home.playground.models.TweetAnalytics;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by cuong on 11/22/15.
 */
@Controller
public class TweetResource
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TweetResource.class);

    @Autowired
    private JavaRDD<TweetAnalytics> tweetRdd;

    @RequestMapping(value="/")
    public String dashboard()
    {
        LOGGER.info("Get dashboard page");

        return "views/dashboard/page.html";
    }

    @ResponseBody
    @RequestMapping(value="/analyze/{hashTag}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SentimentAnalytics> analyze(@PathVariable("hashTag") String hashTag)
    {
        LOGGER.info("Sentiment analysis of " + hashTag);
        final Function<TweetAnalytics, Boolean> byHashTag =
            t -> t.getHashTag().equals(hashTag);

        return analyzeTweetAnalyticsRDD(tweetRdd.filter(byHashTag));
    }

    @ResponseBody
    @RequestMapping(value="/analyze")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SentimentAnalytics> analyze(@RequestParam("hashTag")String hashTag, @RequestParam("start")Long start, @RequestParam("end")Long end)
    {
        LOGGER.info("Sentiment analysis of " + hashTag + " between " + start + " and " + end);

        final Function<TweetAnalytics, Boolean> byHashTag =
            t -> t.getHashTag().equals(hashTag);

        final Function<TweetAnalytics, Boolean> byCreatedDate =
            t -> 0 < t.getCreatedDate().compareTo(new Timestamp(start)) && t.getCreatedDate().compareTo(new Timestamp(end)) < 0;

        return analyzeTweetAnalyticsRDD(
            tweetRdd.filter(t -> byHashTag.call(t) && byCreatedDate.call(t))
        );
    }

    private List<SentimentAnalytics> analyzeTweetAnalyticsRDD(JavaRDD<TweetAnalytics> tweetAnalyticsRDD)
    {
        return tweetAnalyticsRDD.groupBy(TweetAnalytics::getSentiment)
            .mapValues(values -> {
                final Map<String, Map<String, Integer>> ageGroups = Maps.newHashMap(Maps.asMap(
                    Sets.newHashSet("child", "adult18to24", "adult25to34", "adult35to44", "adult45to54", "adult55to64", "adultOver64"),
                    ageGroup -> Maps.newHashMap(Maps.asMap(Sets.newHashSet("male", "female"), gender -> 0))
                ));

                return StreamSupport.stream(values.spliterator(), false)
                    .map(tweetAnalytics -> new AbstractMap.SimpleEntry<>(TweetAnalytics.resolveAgeGroup(tweetAnalytics.getAge()), tweetAnalytics.getGender()))
                    .reduce(
                        ageGroups,
                        (group, pair) -> {
                            group.get(pair.getKey()).compute(pair.getValue(), (gender, count) -> count + 1);

                            return group;
                        },
                        (x, acc) -> acc
                    );
            })
            .map (tuple -> {
                final SentimentAnalytics sentimentAnalytics = new SentimentAnalytics();
                sentimentAnalytics.setSentiment(tuple._1());
                sentimentAnalytics.setFreq(tuple._2());
                sentimentAnalytics.setTotal(tuple._2().entrySet().stream().flatMap(entry -> entry.getValue().values().stream()).reduce(0, (x, y) -> x + y));

                return sentimentAnalytics;
            })
            .collect();
    }

    @ResponseBody
    @RequestMapping(value="/timeline/{hashTag}")
    public  Map<String, List<List<Long>>> hist(@PathVariable("hashTag") String hashTag)
    {
        LOGGER.info("Sentiment timeline of " + hashTag);

        return tweetRdd.filter(it -> it.getHashTag().equals(hashTag))
            .groupBy(TweetAnalytics::getSentiment)
            .mapValues(
                it -> StreamSupport.stream(it.spliterator(), false)
                    .collect(Collectors.groupingBy(TweetAnalytics::getCreatedDate, Collectors.counting()))
                    .entrySet()
                    .stream()
                    .map(entry -> Arrays.asList(entry.getKey().getTime(), entry.getValue()))
                    .collect(Collectors.toList())
            )
            .collectAsMap();
    }
}
