package com.example.vpa

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaos.view.PinView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.TimeUnit


class VerifyPhoneNo : AppCompatActivity() {
    private lateinit var verifyBtn: Button
    private lateinit var pinFromUser: PinView
    private lateinit var sentNoLabel : TextView
    private var codeBySystem: String = ""
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var name:String=""
    var contact:String=""
    var vehNO:String=""
    var vehicle:String=""
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_verify_phone_no)
        verifyBtn = findViewById(R.id.button_verify)
        pinFromUser = findViewById(R.id.pin_view)
        sentNoLabel=findViewById(R.id.sentNo_field)
        name = intent.getStringExtra("name").toString()
        contact = intent.getStringExtra("contact").toString()
        vehNO = intent.getStringExtra("vehNo").toString()
        vehicle = intent.getStringExtra("vehicle").toString()

        sentNoLabel.setText("Enter OTP sent on $contact")

        sendVerificationCodeToUser(contact)

        verifyBtn.setOnClickListener(View.OnClickListener {
            val code: String = pinFromUser.getText().toString()
            if (code.isEmpty() || code.length < 6) {
                pinFromUser.setError("Wrong OTP...")
                pinFromUser.requestFocus()
                return@OnClickListener
            }
            verifyCode(code)
        })


    }
    private fun sendVerificationCodeToUser(contact: String?) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$contact") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }


    private val callbacks: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                codeBySystem = s
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    pinFromUser.setText(code)
                    verifyCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@VerifyPhoneNo, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(codeBySystem, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        var i :Intent
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    //Verification completed successfully here Either
                    // store the data or do whatever desire
                    Toast.makeText(
                        this@VerifyPhoneNo,
                        "Verification Completed!",
                        Toast.LENGTH_SHORT
                    ).show()
                    storeData()
                    val source = intent.getStringExtra("source") ?: ""
                    if(source=="OwnerSignUp") {
                       i = Intent(applicationContext, OwnerProfile::class.java)
                        i.putExtra("name", name)
                        i.putExtra("contact", contact)
                        i.putExtra("vehNo", vehNO)
                        i.putExtra("vehicle", vehicle)
                    }
                    else{
                        i = Intent(applicationContext, OwnerInfo::class.java)
                        i.putExtra("name", name)
                    }
                    startActivity(i)
                    finish()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(
                            this@VerifyPhoneNo,
                            "Verification Not Completed! Try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun storeData() {
        val source = intent.getStringExtra("source") ?: ""
        if(source=="OwnerSignUp") {
            val rootNode: FirebaseDatabase = FirebaseDatabase.getInstance()
            val reference: DatabaseReference = rootNode.getReference("owners")
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                // Get new FCM registration token
               val token = task.result

                val ohelperClass = OwnerHelperClass(name, contact, vehNO, vehicle,token,0)
                reference.child(contact).setValue(ohelperClass)
            })
        }
        else
        {
            val rootNode: FirebaseDatabase = FirebaseDatabase.getInstance()
            val reference: DatabaseReference = rootNode.getReference("informers")
            val ihelperClass = InformerHelperClass(name, contact)
            reference.child(contact).setValue(ihelperClass)
        }
    }
}