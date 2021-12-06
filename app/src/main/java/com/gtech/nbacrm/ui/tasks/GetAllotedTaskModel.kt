package com.gtech.nbacrm.ui.tasks

import com.google.firebase.database.ServerValue
import java.io.Serializable

class GetAllotedTaskModel :Serializable{
    var key:String?=null
    var clientname:String?=null
    var taskname:String?=null
    var taskdate:String?=null
    var allotedby:String?=null
    var allotedto:String?=null
    var taskremark:String?=null
    var allotedtoid:String?=null
    var allotedbyid:String?=null
    var status:String="PENDING"
    var startdate:String?=null
    var enddate:String?=null
    var timestamp:Long?=null
}