package com.example.vpa

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*

class InformerSignUp : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val reference = database.getReference("informers")

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var informerName: TextInputLayout
    private lateinit var informerContno: TextInputLayout
    private fun isUser() {
        val icontno: TextInputLayout = findViewById(R.id.informer_contno)
        val value = icontno.editText?.text.toString()
        val query: Query = reference.orderByChild("contact").equalTo(value)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val contactExists = dataSnapshot.exists()
                if (contactExists) {
                    icontno.error = "Contact already exists!"
                    icontno.isErrorEnabled = true
                } else {
                    icontno.error = null
                    icontno.isErrorEnabled = false
                    // Start the activity if the contact doesn't exist
                    startActivity()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@InformerSignUp, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startActivity() {
        // Get all the values
        val enteredName = informerName.editText?.text.toString().trim()
        val enteredContact = informerContno.editText?.text.toString()

        val editor = sharedPreferences.edit()
        editor.putString("iname", enteredName)
        editor.putString("icontact", enteredContact)
        editor.apply()

        val intent = Intent(applicationContext, VerifyPhoneNo::class.java)
        intent.putExtra("source", "InformerSignUp")
        intent.putExtra("name", enteredName)
        intent.putExtra("contact", enteredContact)
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
        setContentView(R.layout.activity_informer_sign_up)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)


        informerName= findViewById(R.id.informer_name)
        informerContno= findViewById(R.id.informer_contno)
        val informerGo: Button =findViewById(R.id.informer_go)

        fun validateIName(): Boolean {
            val value =informerName.editText?.text.toString()
            if (value.isEmpty()) {
                informerName.error = "Field cannot be empty"
                return false
            } else {
                informerName.error = null
                informerName.isErrorEnabled = false
                return true
            }
        }

        fun validateIContact(): Boolean {
            val value = informerContno.editText?.text.toString()
            val noWhiteSpace = "\\A\\w{4,20}\\z"
            if (value.isEmpty()) {
                informerContno.error = "Field cannot be empty"
                return false
            }else if (!value.matches(Regex((noWhiteSpace)))) {
                informerContno.error = "White Spaces are not allowed"
                return false
            }else if(value.length!=10) {
                informerContno.error = "Invalid number!"
                return false
            }
            else {
                informerContno.error = null
                informerContno.isErrorEnabled = false
                return true
            }
        }

        informerGo.setOnClickListener {
            if (!isConnected(this)) {
                showCustomDialog()
            }
            // Validation
            val isnameValidated = validateIName()
            val iscontactValidated = validateIContact()
            if (!isnameValidated || !iscontactValidated) {
                return@setOnClickListener
            }
            else {
                isUser()
            }
        }
    }
    private fun isConnected(informerSignUp: InformerSignUp): Boolean {

        val connectivityManager: ConnectivityManager = informerSignUp.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiCon: NetworkInfo? = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobCon: NetworkInfo? = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return (wifiCon!=null && wifiCon.isConnected()) || (mobCon!=null && mobCon.isConnected())
    }

    private fun showCustomDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@InformerSignUp)
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