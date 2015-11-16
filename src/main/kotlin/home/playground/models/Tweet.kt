package home.playground.models

import java.io.Serializable
import java.sql.Timestamp
import java.util.*

/**
 * Created by cuong on 11/14/15.
 */
data class Tweet (
        val text           : String    = "",
        val profileImageUrl: String    = "",
        val hashTag        : String    = "",
        val createdDate    : Timestamp = Timestamp(Date().time),
        var gender         : String?   = null, // Enum: Male, Female, Unknown
        var sentiment      : String    = "",   // Enum: Pos, Neg, Neu, Unknown
        var age            : Int?      = null,
        val mediaType      : String    = ""
) : Serializable