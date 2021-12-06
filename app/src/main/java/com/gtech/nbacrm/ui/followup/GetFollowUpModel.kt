package com.gtech.nbacrm.ui.followup



import com.google.firebase.database.ServerValue
import com.gtech.nbacrm.ui.shared.CommentModel
import java.io.Serializable
import java.text.DateFormat
import java.text.DateFormat.getDateTimeInstance
import java.util.*

class GetFollowUpModel:Serializable {
    var clientname: String? = null
    var contact: String? = null
    var carpetArea: String? = null
    var key: String? = null
    var location: String? = null
    var email: String? = null
    var updatedat: Long?=null
    var type: String? = null
    var address: String? = null
    var lastcomment: String? = null
    var priority:String?=null
    var lastcall: String? = null
    var lastcalldate: String? = null
    var nextcalldate: String? = null
    var lastcommentdate: String? = null
    var comments:HashMap<String, CommentModel>?=null
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
