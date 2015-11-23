package home.playground.configs;

import com.google.gson.GsonBuilder;
import home.playground.models.TweetAnalytics;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by cuong on 11/22/15.
 */
@Configuration
public class ApplicationConfig
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

    private JavaRDD<TweetAnalytics> tweetRdd;

    private final String sparkStorageDirectory = "/Users/cuong/Downloads/spark-1.5.1/examples/tweet_bank/*";

    @Bean
    public JavaRDD<TweetAnalytics> tweetRdd()
    {
        LOGGER.info("");

        if (tweetRdd == null) {
            final JavaSparkContext sc = new JavaSparkContext(createSparkConf());

            final JavaRDD<String> rdd = sc.textFile(sparkStorageDirectory);

            tweetRdd = rdd.map(s -> new GsonBuilder().create().fromJson(s, TweetAnalytics.class));
            tweetRdd.cache();
            tweetRdd.count();
        }

        return tweetRdd;
    }

    private SparkConf createSparkConf()
    {
        final SparkConf conf = new SparkConf().setMaster("local[*]")
                .setAppName("SparkSQL")
                .set("spark.executor.memory", "1g")
                .set("spark.default.parallelism", "8")
                .set("spark.driver.allowMultipleContexts", "true");

        return conf;
    }
}
