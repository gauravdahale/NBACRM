package com.gtech.nbacrm.ui.leads


import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.encoders.annotations.ExtraProperty
import com.gtech.nbacrm.ui.shared.CommentModel
import java.io.Serializable
import java.text.DateFormat
import java.text.DateFormat.getDateTimeInstance
import java.util.*
import kotlin.collections.HashMap
@IgnoreExtraProperties
class ReadLeadModel:Serializable {
    var clientname: String? = null
    var key: String? = null
    var contact: String? = null
    var carpetArea: String? = null
    var location: String? = null
    var address: String? = null
    var updatedat:Long?=null
    var lastcalldate: String? = null
    var nextcalldate: String? = null
    var priority: String? = null
    var lastcomment: String? = null
    var comments:HashMap<String,CommentModel>?=null
    var date: String? = null
    var timestamp: Long?=null

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
