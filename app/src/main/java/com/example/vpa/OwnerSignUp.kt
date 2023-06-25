package com.example.vpa

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.ValueEventListener
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging

class OwnerSignUp : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val reference = database.getReference("owners")

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var ownerName: TextInputLayout
    private lateinit var ownerContno: TextInputLayout
    private lateinit var ownerVehno: TextInputLayout
    private lateinit var ownerVehicle: TextInputLayout
    private lateinit var ownerGo: Button
    private fun isUser() {
        val ownercontno: TextInputLayout = findViewById(R.id.owner_contno)
        val value = ownercontno.editText?.text.toString()
        val query: Query = reference.orderByChild("contact").equalTo(value)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val contactExists = dataSnapshot.exists()
                if (contactExists) {
                    ownercontno.error = "Contact already exists!"
                    ownercontno.isErrorEnabled = true
                } else {
                    ownercontno.error = null
                    ownercontno.isErrorEnabled = false
                    // Start the activity if the contact doesn't exist
                    startActivity()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@OwnerSignUp, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startActivity() {

        // Get all the values
        val enteredName = ownerName.editText?.text.toString().trim()
        val enteredContact = ownerContno.editText?.text.toString()
        val enteredVehNo = ownerVehno.editText?.text.toString().trim()
        val enteredVehColor = ownerVehicle.editText?.text.toString().trim()

        val editor = sharedPreferences.edit()
        editor.putString("oname", enteredName)
        editor.putString("ocontact", enteredContact)
        editor.putString("vehNo", enteredVehNo)
        editor.putString("vehicle", enteredVehColor)
        editor.apply()


        val intent = Intent(applicationContext, VerifyPhoneNo::class.java)
        intent.putExtra("source", "OwnerSignUp")
        intent.putExtra("name", enteredName)
        intent.putExtra("contact", enteredContact)
        intent.putExtra("vehNo", enteredVehNo)
        intent.putExtra("vehicle", enteredVehColor)
        startActivity(intent)
        finish()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_owner_sign_up)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        ownerName = findViewById(R.id.owner_name)
        ownerContno = findViewById(R.id.owner_contno)
        ownerVehno = findViewById(R.id.owner_vehno)
        ownerVehicle = findViewById(R.id.owner_vehicle)
        ownerGo = findViewById(R.id.owner_go)

        fun validateOName(): Boolean {
            val value = ownerName.editText?.text.toString()
            if (value.isEmpty()) {
                ownerName.error = "Field cannot be empty"
                return false
            } else {
                ownerName.error = null
                ownerName.isErrorEnabled = false
                return true
            }
        }

        fun validateOContact(): Boolean {
            val value = ownerContno.editText?.text.toString()
            val noWhiteSpace = "\\A\\w{4,20}\\z"

            if (value.isEmpty()) {
                ownerContno.error = "Field cannot be empty"
                return false
            } else if (!value.matches(Regex((noWhiteSpace)))) {
                ownerContno.error = "White Spaces are not allowed"
                return false
            } else if (value.length != 10) {
                ownerContno.error = "Invalid number!"
                return false
            } else {
                ownerContno.error = null
                ownerContno.isErrorEnabled = false
                return true
            }
        }

        fun validateVehno(): Boolean {
            val value = ownerVehno.editText?.text.toString()
            val regexPattern = "[A-Z]{2} \\d{1,2}[A-Z]{1,2} \\d{1,4}"
            if (value.isEmpty()) {
                ownerVehno.error = "Field cannot be empty"
                return false
            } else if (!value.matches(Regex((regexPattern)))) {
                ownerVehno.error = "Invalid Pattern! Follow : AB 12CD 3456"
                return false
            } else {
                ownerVehno.error = null
                ownerVehno.isErrorEnabled = false
                return true
            }
        }

        fun validateVehicle(): Boolean {
            val value = ownerVehicle.editText?.text.toString()
            if (value.isEmpty()) {
                ownerVehicle.error = "Field cannot be empty"
                return false
            } else {
                ownerVehicle.error = null
                ownerVehicle.isErrorEnabled = false
                return true
            }
        }

        ownerGo.setOnClickListener {

            if (!isConnected(this)) {
                showCustomDialog()
            }

                // Validation
                val isNameValidated = validateOName()
                val isContactValidated = validateOContact()
                val isNoValidated = validateVehno()
                val isColorValidated = validateVehicle()

                if (!isNameValidated || !isContactValidated || !isNoValidated || !isColorValidated) {
                    return@setOnClickListener
                } else {
                    isUser()
                }

            }
        }

    private fun isConnected(ownerSignUp: OwnerSignUp): Boolean {

        val connectivityManager:ConnectivityManager = ownerSignUp.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiCon: NetworkInfo? = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobCon: NetworkInfo? = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return (wifiCon!=null && wifiCon.isConnected()) || (mobCon!=null && mobCon.isConnected())
    }

    private fun showCustomDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@OwnerSignUp)
        builder.setMessage("Please connnect to the Internet")
            .setCancelable(false)
            .setPositiveButton("Connect") { dialog, _ ->
                val settingsIntent = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)
                startActivity(settingsIntent)
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                onBackPressed()
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(resources.getColor(android.R.color.white))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(resources.getColor(android.R.color.white))
    }
}