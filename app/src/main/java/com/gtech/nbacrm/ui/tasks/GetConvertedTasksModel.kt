package com.gtech.nbacrm.ui.tasks


import com.google.firebase.database.ServerValue
import com.gtech.nbacrm.ui.shared.CommentModel
import java.io.Serializable
import java.text.DateFormat
import java.util.*

class GetConvertedTasksModel : Serializable {
    var key: String? = null
    var name: String? = null
    var tasktype: String? = ""
    var comment: String? = null
    var done: Boolean? = false
    var confirm: Boolean = false
    var comments: HashMap<String, CommentModel>? = null

    var type = 1
    var doneby: String? = null
    var timestamp: Long? = null
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