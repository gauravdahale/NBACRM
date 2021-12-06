package com.gtech.nbacrm.ui.tasks

import com.google.firebase.database.ServerValue

class AllotedTaskModel {
var clientname:String?=null
var taskname:String?=null
var taskremark:String?=null
var taskdate:String?=null
var allotedby:String?=null
var allotedto:String?=null
var allotedtoid:String?=null
var allotedtotoken:String?=null
var allotedbyid:String?=null
var status:String="PENDING"
var startdate:String?=null
var enddate:String?=null
var timestamp=ServerValue.TIMESTAMP
}