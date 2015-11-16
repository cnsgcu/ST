package home.playground.configs

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import home.playground.Startup
import home.playground.models.Tweet
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.Duration
import org.apache.spark.streaming.api.java.JavaStreamingContext
import org.apache.spark.streaming.twitter.TwitterUtils
import twitter4j.TwitterFactory
import twitter4j.auth.Authorization
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.ConfigurationBuilder
import java.sql.Timestamp

/**
 * Created by cuong on 11/15/15.
 */
object Config
{
    private var twitterAuth: Authorization? = null

    private var sqlContext: SQLContext? = null
    private var javaSparkContext: JavaSparkContext? = null
    private var streamingContext: JavaStreamingContext? = null

    val gson: Gson = GsonBuilder().create()

    fun configureSqlContext(): SQLContext {
        if (sqlContext == null) {
            configureJavaSparkContext()

            sqlContext = SQLContext(streamingContext?.sparkContext());

            val tweetRDD = javaSparkContext!!.textFile("/Users/cuong/Downloads/spark-1.5.1/examples/piggy_bank/*")
                    .map { Config.gson.fromJson(it, Tweet::class.java) }

            val dfTweet = sqlContext!!.createDataFrame(tweetRDD, Tweet::class.java)
            dfTweet.registerTempTable("tweets")
        }

        return sqlContext!!
    }

    fun configureJavaSparkContext(): JavaSparkContext {
        Logger.getLogger("org").level = Level.OFF
        Logger.getLogger("akka").level = Level.OFF
        Logger.getLogger("twitter4j").level = Level.OFF

        if (javaSparkContext == null) {
            val conf = SparkConf().setMaster("local[5]")
                    .setAppName("SparkSQL")
                    .set("spark.driver.allowMultipleContexts", "true")

            streamingContext = JavaStreamingContext(conf, Duration(3000))
            //Thread{ collectTweet(streamingContext!!, configureTwitterAuth()) }.start()

            javaSparkContext = streamingContext!!.sparkContext()
        }

        return javaSparkContext!!
    }

    fun configureTwitterAuth(): Authorization
    {
        if (twitterAuth == null) {
            val authConfig = ConfigurationBuilder()
                    .setOAuthConsumerKey("QKoRQCFQ8EoJUAugCZ5ZDCFZI")
                    .setOAuthConsumerSecret("cIhK0hnyX4eZb8JakQUSYzueYJRhyyE8v7Yfitg5VhLDcm7rrT")
                    .setOAuthAccessToken("2732661193-kcBEZEZr1JD8R5FUoTn69QqVXSy3xBxovehKSQk")
                    .setOAuthAccessTokenSecret("4E6gRkzJugA8x8o2IWLKFGDYmO5reEWRXPgDi5UwEmWLv")
                .build()

            val authFactory = TwitterFactory(authConfig)

            twitterAuth = authFactory.getInstance(OAuthAuthorization(authConfig)).authorization
        }

        return twitterAuth!!
    }

    fun collectTweet(streamingContext: JavaStreamingContext, twitterAuth: Authorization)
    {
        val tweetStream = TwitterUtils.createStream(streamingContext, twitterAuth)

        tweetStream.foreachRDD { statusRDD, time ->
            val tweetRDD = statusRDD
                    .filter { it.user.lang.equals("en") && it.hashtagEntities.isNotEmpty() }
                    .map {
                        val tweet = Tweet(text = it.text, profileImageUrl = it.user.profileImageURL, createdDate = Timestamp(it.createdAt.time), hashTag = it.hashtagEntities.first().text)
//                      faceAnalysis(tweet)
                        gson.toJson(tweet)
                    }

            tweetRDD.saveAsTextFile("/Users/cuong/Downloads/spark-1.5.1/examples/piggy_bank/${time.milliseconds()}")

            null
        }

        streamingContext.start()
        streamingContext.awaitTermination()
    }
}