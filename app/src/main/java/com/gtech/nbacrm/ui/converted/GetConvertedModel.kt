package com.gtech.nbacrm.ui.converted

import com.google.firebase.database.ServerValue
import com.gtech.nbacrm.ui.shared.CommentModel
import com.gtech.nbacrm.ui.tasks.ConvertedTasksModel
import com.gtech.nbacrm.ui.tasks.GetConvertedTasksModel
import java.io.Serializable
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class GetConvertedModel: Serializable {
    var clientname: String? = null
    var contact: String? = null
    var key: String? = null
    var carpetArea: String? = null
    var location: String? = null
    var email: String? = null
    var address: String? = null
    var type :String?= "Preconsultation"
    var lastcalldate: String? = null
    var priority: String? = null
    var lastcomment: String? = null
    var date: String? = null
    var lastcall: String? = null
    var timestamp: Long?=null
    var updatedat: Long?=null
    var comments:HashMap<String, CommentModel>?=null
    var tasks :ArrayList<GetConvertedTasksModel>?=null

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
