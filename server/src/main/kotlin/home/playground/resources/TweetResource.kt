package home.playground.resources

import home.playground.models.*
import home.playground.services.FaceAnalysisService
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.SQLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
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

    @Autowired
    private var javaSparkContext: JavaSparkContext? = null

    @Autowired
    private var sqlContext: SQLContext? = null

    private val fas = FaceAnalysisService()

    @RequestMapping(value="/")
    public fun dashboard(): String?
    {
        LOGGER.info("Get dashboard page")

        return "dashboard/page"
    }

    @ResponseBody
    @RequestMapping(value="/top")
    @Produces(MediaType.APPLICATION_JSON)
    public fun top(): List<HashTagStatistic>
    {
        LOGGER.info("Return top 10 hashtags")

        val hashTags = sqlContext!!.sql("SELECT hashTag, count(*) as count FROM tweets GROUP BY hashTag ORDER BY count desc LIMIT 10")

        return hashTags.javaRDD().collect().map { HashTagStatistic(hashTag = it.getString(0), count = it.getLong(1)) }
    }

    @ResponseBody
    @RequestMapping(value="/analyze/{hashTag}")
    @Produces(MediaType.APPLICATION_JSON)
    public fun analyze(@PathVariable("hashTag") hashTag: String): List<SentimentStatistic>
    {
        LOGGER.info("Sentiment analysis of $hashTag")

        val sentHist = HashMap<String, SentimentStatistic>()

        sqlContext!!.sql("SELECT sentiment, gender, age, count(*) as count FROM tweets WHERE hashTag = '$hashTag' GROUP BY sentiment, gender, age")
            .javaRDD().collect().forEach {
                val sent     = it.getString(0)
                val gender   = it.getString(1)
                val ageGroup = resolveAgeGroup(it.getInt(2))
                val count    = it.getLong(3)

                if (!sentHist.containsKey(sent)) {
                    sentHist.put(sent, SentimentStatistic(sentiment = sent))
                }

                val sentEntry = sentHist[sent] as SentimentStatistic
                val ageGroupEntry = sentEntry.freq.getRaw(ageGroup) as HashMap<String, Long>

                sentEntry.total += count
                ageGroupEntry.put(gender, ageGroupEntry[gender] as Long + count)
            }

        return sentHist.values.toList()
    }

    @ResponseBody
    @RequestMapping(value="/recognize")
    @Produces(MediaType.APPLICATION_JSON)
    public fun recognize(@RequestParam("imgUrl") imgUrl: String): FaceRecognization
    {
        /**
         * {
         *  positive: Array[Array[2], ..., Array[2]],
         *  negative: {...}
         * }
         */
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