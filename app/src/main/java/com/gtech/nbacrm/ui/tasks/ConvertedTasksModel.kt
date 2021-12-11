package com.gtech.nbacrm.ui.tasks

import com.google.firebase.database.ServerValue
import java.io.Serializable

class ConvertedTasksModel constructor() : Serializable {

    var name: String? = null
    var done: Boolean = false
    var confirm: Boolean = false
    var comment: String? = ""
    var doneby: String? = null
    var tasktype: String? = null
    var timestamp: MutableMap<String, String>? = null
constructor(name: String,don:Boolean) : this() {
    this.name = name
    this.done =don
}
}
