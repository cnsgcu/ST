package home.playground.resources

import home.playground.models.*
import home.playground.services.FaceAnalysisService
import org.apache.spark.api.java.JavaRDD
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import scala.Tuple2
import java.io.Serializable
import java.util.*
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType


/**
 * Created by cuong on 11/14/15.
 */
@Controller
class TweetResource
{
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TweetResource::class.java)
    }

    private val fas = FaceAnalysisService()

    @Autowired
    private var tweetRdd: JavaRDD<Tweet>? = null

    @RequestMapping(value="/")
    public fun dashboard(): String?
    {
        LOGGER.info("Get dashboard page")

        return "views/dashboard/page.html"
    }

    @ResponseBody
    @RequestMapping(value="/top")
    @Produces(MediaType.APPLICATION_JSON)
    public fun top(): List<HashTagStatistic>
    {
        LOGGER.info("Return top 10 hashtags")

        return tweetRdd!!.groupBy { it.hashTag }
            .mapValues { it.count() }
            .top(10, object : Comparator<Tuple2<String, Int>>, Serializable {
                override fun compare(lhs: Tuple2<String, Int>?, rhs: Tuple2<String, Int>?): Int {
                    return lhs?._2!! - rhs?._2!!
                }
            }).map { HashTagStatistic(hashTag = it._1, count = it._2) }
    }

    @ResponseBody
    @RequestMapping(value="/analyze/{hashTag}")
    @Produces(MediaType.APPLICATION_JSON)
    public fun analyze(@PathVariable("hashTag") hashTag: String): List<SentimentStatistic>
    {
        LOGGER.info("Sentiment analysis of $hashTag")

        return tweetRdd!!.filter { it.hashTag == hashTag }
            .groupBy { it.sentiment }
            .collectAsMap()
            .mapValues {
                it.value.groupBy { resolveAgeGroup(it.age as Int) }
                        .mapValues {
                            it.value.partition { it.gender == "male" }
                                    .let { mapOf("male" to it.first.size, "female" to it.second.size) }
                        }
            }
            .map {
                SentimentStatistic(
                    freq = it.value,
                    sentiment = it.key,
                    total = it.value.values.fold(0) { total, ageGroup -> total + ageGroup.values.sum() }
                )
            }
            .toList()
    }

    @ResponseBody
    @RequestMapping(value="/recognize")
    @Produces(MediaType.APPLICATION_JSON)
    public fun recognize(@RequestParam("imgUrl") imgUrl: String): FaceRecognization
    {
        return fas.faceAnalysis(imgUrl)
    }

    private fun resolveAgeGroup(age: Int): String
    {
        if (age < 18) {
            return "child"
        } else if (age <= 24) {
            return "adult18to24"
        } else if (age <= 34) {
            return "adult25to34"
        } else if (age <= 44) {
            return "adult35to44"
        } else if (age <= 54) {
            return "adult45to54"
        } else if (age <= 64) {
            return "adult55to64"
        } else {
            return "adultOver64"
        }
    }
}