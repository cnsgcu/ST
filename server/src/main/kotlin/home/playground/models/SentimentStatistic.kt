package home.playground.models

import java.io.Serializable
import java.util.*

/**
 * Created by cuong on 11/15/15.
 */

data class SentimentStatistic(
    var total: Long = 0L,
    var sentiment: String = "",
    var freq: HashMap<String, HashMap<String, Long>> = hashMapOf(
        "child" to hashMapOf(
            "male"   to 0L,
            "female" to 0L
        ),
        "adult18to24" to hashMapOf(
            "male"   to 0L,
            "female" to 0L
        ),
        "adult25to34" to hashMapOf(
            "male"   to 0L,
            "female" to 0L
        ),
        "adult35to44" to hashMapOf(
            "male"   to 0L,
            "female" to 0L
        ),
        "adult45to54" to hashMapOf(
            "male"   to 0L,
            "female" to 0L
        ),
        "adult55to64" to hashMapOf(
            "male"   to 0L,
            "female" to 0L
        ),
        "adultOver64"  to hashMapOf(
            "male"   to 0L,
            "female" to 0L
        )
    )
) : Serializable