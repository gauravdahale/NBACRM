package com.gtech.nbacrm

import android.app.Application
import android.content.Context
import com.google.firebase.database.FirebaseDatabase


class App : Application() {
    val TAG ="Application"
    var mDatabase: FirebaseDatabase? = null

    override fun onCreate() {
        super.onCreate()
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase?.setPersistenceEnabled(true);

        }

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }


    companion object {
        var context: Context? = null
            private set
    }
}