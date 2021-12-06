package com.gtech.nbacrm.ui.auth

import com.gtech.nbacrm.R

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.messaging.FirebaseMessaging
import com.gtech.nbacrm.MainActivity
//import com.google.firebase.messaging.FirebaseMessaging
//import com.gtech.zodejee.R
//import com.gtech.models.UserModel
//import com.gtech.zodejee.activity.MainActivity
//import kotlinx.android.synthetic.main.activity_customer_info.*
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken as ForceResendingToken

class LoginActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    lateinit var mResendToken1: ForceResendingToken
    private var mVerificationId: String? = null
    private var mDatabaseReference: DatabaseReference? = null
    private lateinit var mobile: String
    private lateinit var name: String
    private lateinit var address: String
    private val sharedprefFile = "shop_selected"
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        mDatabaseReference = FirebaseDatabase.getInstance().reference
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


        submit_number.setOnClickListener {
            if (!auth_mobile.editText?.text.isNullOrEmpty() && auth_mobile.editText?.text!!.length >= 10 && !auth_name.editText?.text.toString().isBlank()) {
                mobile = auth_mobile.editText?.text.toString()
                name = auth_name.editText?.text.toString()
                //address = auth_address.editText?.text.toString()
                Toast.makeText(this, "Mobile : $mobile", Toast.LENGTH_SHORT).show()
                sendVerificationCode(mobile)
                step1.visibility = View.GONE
                step2.visibility = View.VISIBLE
            } else Toast.makeText(this, "Please Enter a Valid Number", Toast.LENGTH_SHORT).show()
        }
        sumbit_otp.setOnClickListener {
            val code = otptext?.text.toString().trim { it <= ' ' }
            if (code.isEmpty() || code.length < 6) {
                editTextCode?.error = "Enter valid code"
                editTextCode?.requestFocus()
                return@setOnClickListener
            }
            //verifying the code entered manually
            try {
                verifyVerificationCode(code)
            } catch (e: Exception) {
                Log.d("submitotp", "${e.message}")
            }

        }

        resendotp.setOnClickListener { resendVerificationCode(mobile, mResendToken1) }


    }

    //the method is sending verification code
//the country id is concatenated
//you can take the country id as user input as well

    private fun sendVerificationCode(mobile: String?) {

        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber("+91$mobile")
            .setTimeout(30L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallbacks)
            .build();
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun resendVerificationCode(
        mobile: String?,
        mResendToken: ForceResendingToken?
    ) {

        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber("+91$mobile")
            .setTimeout(30L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallbacks)
            .setForceResendingToken(mResendToken!!)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
//        getInstance().verifyPhoneNumber(
//            "+91$mobile",  // Phone number to verify
//            30,  // Timeout duration
//            TimeUnit.SECONDS,  // Unit of timeout
//            this,  // Activity (for callback binding)
//            mCallbacks,  // OnVerificationStateChangedCallbacks
//            mResendToken
//        ) // ForceResendingToken from callbacks
        Toast.makeText(this, "OTP has been sent again", Toast.LENGTH_LONG).show()
    }

    //the callback to detect the verification status
    private val mCallbacks: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) { //Getting the code sent by SMS
                val code = phoneAuthCredential.smsCode
                //sometime the code is not detected automatically
//in this case the code will be null
//so user has to manually enter the code
                if (code != null) {
                    otptext!!.setText(code)
                    //verifying the code
                    verifyVerificationCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                //storing the verification id that is sent to the user
                mVerificationId = s
                mResendToken1 = forceResendingToken

            }
        }

    private fun verifyVerificationCode(code: String) { //creating the credential
//signing the user
        try {
            val credential = getCredential((mVerificationId)!!, code)
            signInWithPhoneAuthCredential(credential)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "verifyVerificationCode" + e.localizedMessage)
            // startActivity( new Intent(VerifyPhoneActivity.this,DrawerActivity.class));
            finish()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) { //verification successful we will start the profile activity
                    //User Data Feeding to database.
//                    val tags = JSONObject()
//                    try {
//                        tags.put("Id", token)
//                        tags.put("Name", name)
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                    OneSignal.sendTags(tags)
                    val datetime =
                        DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
                    NewUser(
                        mobile!!.trim { it <= ' ' },
                        name,
                        //address
                    )

                } else { //verification unsuccessful.. display an error message
                    var message = "Something is wrong, we will fix it soon..."
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        message = "Invalid code entered..."
                    }
                    val snackbar =
                        Snackbar.make(
                            findViewById(R.id.auth_layout),
                            (message),
                            Snackbar.LENGTH_LONG
                        )
                    snackbar.setAction("Dismiss") { }
                    snackbar.show()
                }
            }
    }

    private fun NewUser(
        newuserNumber: String,
        newusername: String,

    ) { //Creating a movie object with user defined variables
        val user = UserModel()
//        user.userOccupation = newuserOccupation
//        user.userNumber = newuserNumber
        user.name = newusername
        user.timestamp = ServerValue.TIMESTAMP
//        user.address = address
//        user.token = newtoken
        user.number = newuserNumber
        //referring to movies node and setting the values from movie object to that location


//        mDatabaseReference!!.child("users").push().setValue(user)

        mDatabaseReference!!.child("users").child(mAuth!!.currentUser!!.uid).setValue(user).addOnSuccessListener {


            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                user.token = token
                mDatabaseReference!!.child("users").child(mAuth!!.currentUser!!.uid)
                    .child("usertoken")
                    .setValue(token)

                // Log and toast
                val msg = "Generated Token: $token"
                Log.d(TAG, msg.toString())
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })

            val mAnalytics = FirebaseAnalytics.getInstance(this)
            val bundle = Bundle()
            bundle.putString("NewUser", newuserNumber)
            mAnalytics.logEvent("NewUsers", bundle)
            // Get current version code
//        val username = newuserName.trim { it <= ' ' }
            val userphone = newuserNumber.trim { it <= ' ' }
            // Get saved version code
            val prefs = getSharedPreferences(sharedprefFile, Context.MODE_PRIVATE)
            val editor = prefs.edit()
//        editor.putString("USER_NAME", username)
            editor.putString("USER_NUMBER", newuserNumber)
            editor.putString("USER_NAME", newusername)
          //  editor.putString("address", address)
            editor.apply()
val usernamesaved = prefs.getString("USER_NAME","")
           Log.d("NewUser","$usernamesaved")
            val intent = Intent(this, MainActivity::class.java)
            Toast.makeText(applicationContext, "Login Successfull", Toast.LENGTH_SHORT)
                .show()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
            .addOnFailureListener {
                Log.d("NewUser","msg: ${it.message}")
            }
    }

    companion object {
        private val TAG = "VerifyPhoneActivity"
    }
}
