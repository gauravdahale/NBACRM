//package gauravdahale.gtech.akolaadmin.fcm
//
//import android.content.Intent
//import android.net.Uri
//import android.util.Log
//import android.widget.Toast
//
//import com.onesignal.OSNotificationAction
//import com.onesignal.OSNotificationOpenResult
//import com.onesignal.OneSignal
//
//import gauravdahale.gtech.akolaadmin.App
//import gauravdahale.gtech.akolaadmin.activity.MainActivityTabVersion
//import gauravdahale.gtech.akolaadmin.activity.NotificationActivity
//import gauravdahale.gtech.akolaadmin.activity.PromoActivity
//import gauravdahale.gtech.akolaadmin.activity.RecordActivity
//
///**
// * Created by androidbash on 12/14/2016.
// */
//
//class MyNotificationOpenedHandler : OneSignal.NotificationOpenedHandler {
//    // This fires when a notification is opened by tapping on it.
//    override fun notificationOpened(result: OSNotificationOpenResult) {
//        val actionType = result.action.type
//        val data = result.notification.payload.additionalData
//        val activityToBeOpened: String?
//        val n: String
//        val a: String
//        val k: String
//        val p: String
//        val d: String
//        val i: String
//        val ii: String
//        val iii: String
//        val iiii: String
//        val o: String
//        val u: String
//        //While sending a Push notification from OneSignal dashboard
//        // you can send an addtional data named "activityToBeOpened" and retrieve the value of it and do necessary operation
//        //If key is "activityToBeOpened" and value is "AnotherActivity", then when a user clicks
//        //on the notification, AnotherActivity will be opened.
//        //Else, if we have not set any additional data MainActivity is opened.
//        if (data != null) {
//            activityToBeOpened = data.optString("activityToBeOpened", null)
//            n = data.optString("n", null)
//            a = data.optString("a", null)
//            p = data.optString("p", null)
//            i = data.optString("i", null)
//            ii = data.optString("ii", null)
//            iii = data.optString("iii", null)
//            iiii = data.optString("iiii", null)
//            d = data.optString("d", null)
//            o = data.optString("o", null)
//            u = data.optString("u", null)
//            if (activityToBeOpened != null && activityToBeOpened == "AnotherActivity") {
//                Log.i("OneSignalExample", "customkey set with value: $activityToBeOpened")
//                val intent = Intent(gauravdahale.gtech.akolaadmin.App.getContext(), RecordActivity::class.java)
//
//                intent.putExtra("recordtitle", n)
//                intent.putExtra("address", a)
//                intent.putExtra("phone", p)
//                intent.putExtra("url", i)
//                intent.putExtra("url1", ii)
//                intent.putExtra("url2", iii)
//                intent.putExtra("url3", iii)
//                intent.putExtra("desc", d)
//                intent.putExtra("Owner", o)
//                intent.flags =
//                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//                gauravdahale.gtech.akolaadmin.App.getContext().startActivity(intent)
//            }
//            if (activityToBeOpened != null && activityToBeOpened == "url") {
//                /*Intent intent = new Intent(App.getContext(), UrlActivity.class);
//                intent.putExtra("weburl",u);
//                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                App.getContext().startActivity(intent);*/
//                val `in` = Intent(Intent.ACTION_VIEW)
//                `in`.data = Uri.parse(u)
//                gauravdahale.gtech.akolaadmin.App.getContext().startActivity(`in`)
//
//            } else if (activityToBeOpened != null && activityToBeOpened == "Record") {
//                Log.i("OneSignalExample", "customkey set with value: $n")
//                val intent = Intent(gauravdahale.gtech.akolaadmin.App.getContext(), gauravdahale.gtech.akolaadmin.activity.NotificationActivity::class.java)
//                intent.putExtra("recordtitle", n)
//                intent.putExtra("address", a)
//                intent.putExtra("phone", p)
//                intent.putExtra("url", i)
//                intent.putExtra("url1", ii)
//                intent.putExtra("url2", iii)
//                intent.putExtra("url3", iiii)
//                intent.putExtra("desc", d)
//                intent.putExtra("Owner", o)
//                intent.flags =
//                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//                gauravdahale.gtech.akolaadmin.App.getContext().startActivity(intent)
//            } else if (activityToBeOpened != null && activityToBeOpened == "promo") {
//                Log.i("OneSignalExample", "customkey set with value: $n")
//                val intent = Intent(gauravdahale.gtech.akolaadmin.App.getContext(), gauravdahale.gtech.akolaadmin.activity.PromoActivity::class.java)
//
//                intent.putExtra("url", i)
//
//                intent.putExtra("desc", d)
//
//                intent.flags =
//                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//                gauravdahale.gtech.akolaadmin.App.getContext().startActivity(intent)
//            } else {
//                val intent = Intent(gauravdahale.gtech.akolaadmin.App.getContext(), gauravdahale.gtech.akolaadmin.activity.MainActivityTabVersion::class.java)
//                intent.flags =
//                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK
//                gauravdahale.gtech.akolaadmin.App.getContext().startActivity(intent)
//            }
//
//        }
//
//        //If we send notification with action buttons we need to specidy the button id's and retrieve it to
//        //do the necessary operation.
//        if (actionType == OSNotificationAction.ActionType.ActionTaken) {
//            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID)
//            if (result.action.actionID == "s") {
//                val share = Intent(Intent.ACTION_SEND)
//                share.type = "text/plain"
//                share.putExtra(
//                    "android.intent.extra.TEXT",
//                    "https://play.google.com/store/apps/details?id=gauravdahale.gtech.akolaadmin "
//                )
//                share.putExtra(
//                    "android.intent.extra.SUBJECT",
//                    "Download Akola Directory Today and Get Local Offers Right On Your PhoneScreen"
//                )
//
//                gauravdahale.gtech.akolaadmin.App.getContext().startActivity(Intent.createChooser(share, "Akola Directory"))
//            } else if (result.action.actionID == "ActionTwo") {
//                Toast.makeText(gauravdahale.gtech.akolaadmin.App.getContext(), "ActionTwo Button was pressed", Toast.LENGTH_LONG)
//                    .show()
//            }
//        }
//    }
//}