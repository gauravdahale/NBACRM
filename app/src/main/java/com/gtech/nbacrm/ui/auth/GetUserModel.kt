package com.gtech.nbacrm.ui.auth


import com.google.firebase.database.ServerValue
import java.text.DateFormat
import java.util.*

class GetUserModel {
    var key :String?=null
    var name :String?=null
    var number :String?=null
    var timestamp:Long? =null
    var usertoken    :String?=null

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
