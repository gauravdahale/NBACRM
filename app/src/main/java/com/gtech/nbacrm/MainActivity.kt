package com.gtech.nbacrm

    import android.app.NotificationChannel
    import android.app.NotificationManager
    import android.content.Intent
    import android.os.Build
    import android.os.Bundle
    import androidx.appcompat.app.AppCompatActivity
    import androidx.navigation.Navigation
    import androidx.navigation.findNavController
    import androidx.navigation.ui.AppBarConfiguration
    import androidx.navigation.ui.navigateUp
    import androidx.navigation.ui.setupActionBarWithNavController
    import androidx.navigation.ui.setupWithNavController
    import com.google.android.material.bottomnavigation.BottomNavigationView
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.ktx.Firebase
    import com.gtech.nbacrm.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {
 val currentuser  = FirebaseAuth.getInstance().currentUser?.uid
    lateinit var appBarConfiguration :AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(currentuser == null ){
            val intent = Intent(this,LoginActivity::class.java)
            intent.flags =Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        finish()
        }
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val actionBar = actionBar;

        actionBar?.setDisplayHomeAsUpEnabled(true);


        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
         appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_dashboard,R.id.navigation_tasks,
                R.id.navigation_leads, R.id.navigation_follow_up, R.id.navigation_converted))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
  //FCM
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName, NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}