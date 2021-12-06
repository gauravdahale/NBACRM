package com.gtech.nbacrm.ui.leads

import com.google.firebase.database.ServerValue
import com.gtech.nbacrm.ui.shared.CommentModel
import java.io.Serializable
import java.text.DateFormat
import java.text.DateFormat.getDateTimeInstance
import java.util.*
import kotlin.collections.ArrayList

class LeadModel : Serializable {
    var clientname: String? = null
    var contact: String? = null
    var carpetArea: String? = null
    var location: String? = null
    var email: String? =null
    var address: String? = null

    var lastcalldate: String? = null
    var priority: String? = null
    var nextcalldate: String? = null
    var lastcomment: String? = null
    var comments: ArrayList<CommentModel>? = null
    var date: String? = null
    var timestamp: Map<String, String>? = ServerValue.TIMESTAMP

    fun getTimeDate(timestamp: Long): String? {
        return try {
            val dateFormat: DateFormat = getDateTimeInstance()
            val netDate = Date(timestamp)
            dateFormat.format(netDate)
        } catch (e: Exception) {
            "date"
        }
    }
}
