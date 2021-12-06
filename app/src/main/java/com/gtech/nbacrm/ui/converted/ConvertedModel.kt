package com.gtech.nbacrm.ui.converted

import com.google.firebase.database.ServerValue
import com.gtech.nbacrm.ui.tasks.ConvertedTasksModel
import java.io.Serializable
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList


class ConvertedModel: Serializable {
    var clientname: String? = null
    var contact: String? = null
    var carpetArea: String? = null
    var location: String? = null
    var email: String? = null
    var address: String? = null
    var lastcalldate: String? = null
    var lastcomment: String? = null
    var date: String? = null
    var timestamp: Map<String, String>? = ServerValue.TIMESTAMP
var task :ArrayList<ConvertedTasksModel>?=null

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
