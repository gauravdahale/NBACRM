package com.gtech.nbacrm.ui.shared

import java.io.Serializable
import java.text.DateFormat
import java.util.*

class CommentModel :Serializable {
var comment:String?=null
var timestamp:Long?=null
var commentedby:String?=null

    fun getTimeDate(timestamp: Long): String? {
        return try {
            val dateFormat: DateFormat = DateFormat.getDateTimeInstance()
            val netDate = Date(timestamp)
            dateFormat.format(netDate)
        } catch (e: Exception) {
            "date"
        }
    }

}
