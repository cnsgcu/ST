package home.playground.configs

import com.google.gson.Gson
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.SQLContext

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import twitter4j.auth.Authorization

/**
 * Created by cuong on 11/14/15.
 */
@Configuration
open class ApplicationConfig
{
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ApplicationConfig::class.java)
    }

    @Bean
    open public fun gson(): Gson
    {
        return Config.gson
    }

    @Bean
    open public fun sqlContext(): SQLContext {
        return Config.configureSqlContext()
    }

    @Bean
    open public fun javaSparkContext(): JavaSparkContext {
        LOGGER.info("Configure java spark stream")

        return Config.configureJavaSparkContext()
    }

    @Bean
    open public fun twitterAuth(): Authorization
    {
        LOGGER.info("Configure twitter authorization")

        return Config.configureTwitterAuth()
    }
}