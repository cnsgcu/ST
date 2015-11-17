package home.playground.models

import java.io.Serializable
import java.util.*

/**
 * Created by cuong on 11/15/15.
 */

data class SentimentStatistic(
    var total    : Int = 0,
    var sentiment: String = "",
    var freq     : Map<String, Map<String, Int>> = mapOf(
        "child" to mapOf(
            "male"   to 0,
            "female" to 0
        ),
        "adult18to24" to hashMapOf(
            "male"   to 0,
            "female" to 0
        ),
        "adult25to34" to hashMapOf(
            "male"   to 0,
            "female" to 0
        ),
        "adult35to44" to hashMapOf(
            "male"   to 0,
            "female" to 0
        ),
        "adult45to54" to hashMapOf(
            "male"   to 0,
            "female" to 0
        ),
        "adult55to64" to hashMapOf(
            "male"   to 0,
            "female" to 0
        ),
        "adultOver64"  to hashMapOf(
            "male"   to 0,
            "female" to 0
        )
    )
) : Serializable