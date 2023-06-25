package com.example.vpa

import android.annotation.SuppressLint
import android.content.*
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.*

class OwnerProfile : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var oFullname: TextInputLayout
    private lateinit var oContno: TextInputLayout
    private lateinit var oVehno: TextInputLayout
    private lateinit var oVehcolor: TextInputLayout
    private lateinit var fullnameLabel: TextView
    private lateinit var alertLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_owner_profile)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        oFullname = findViewById(R.id.fullname_profile)
        oContno = findViewById(R.id.contactNo_profile)
        oVehno = findViewById(R.id.vehNo_profile)
        oVehcolor = findViewById(R.id.vehColor_profile)
        fullnameLabel = findViewById(R.id.fullname_field)
        alertLabel = findViewById(R.id.alert_label)

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val ownersRef: DatabaseReference = database.getReference("owners")
        val ownerId = sharedPreferences.getString("ocontact", "")

        val editor = sharedPreferences.edit()
        editor.putBoolean("isoLoggedIn", true)
        editor.apply()

        showAllData()

        val oDelete: ImageView = findViewById(R.id.button_delete)
        oDelete.setOnClickListener {
            if (!isConnected(this)) {
                showCustomDialog()
            } else {
                if (!ownerId.isNullOrEmpty()) {
                    ownersRef.child(ownerId).removeValue()
                        .addOnSuccessListener {
                            // Owner successfully removed from the database
                            // Navigate back to the Login activity
                            editor.putBoolean("isoLoggedIn", false)
                            editor.apply()
                            val intent = Intent(this, Login::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@OwnerProfile,
                                "Failed to Delete.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                }
            }
        }

    }

    private fun showAllData() {
        val fullname = sharedPreferences.getString("oname", "")
        val contact = sharedPreferences.getString("ocontact", "")
        val vehicleNo = sharedPreferences.getString("vehNo", "")
        val color = sharedPreferences.getString("vehicle", "")
        val alert:String=""

        val rootNode: FirebaseDatabase = FirebaseDatabase.getInstance()
        val reference: DatabaseReference = rootNode.getReference("owners")

// Retrieve the notify value based on the known contact
        val contactRef: DatabaseReference = reference.child(contact.toString())
        contactRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val owner = dataSnapshot.getValue(OwnerHelperClass::class.java)
                    val notify = owner?.notify

                    // Set the value of notify in the text field
                    alertLabel.text= notify.toString()
                } else {
                    // Handle the case when the contact does not exist in the database
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })



        fullnameLabel.text = fullname
        oFullname.editText?.setText(fullname)
        oContno.editText?.setText(contact)
        oVehno.editText?.setText(vehicleNo)
        oVehcolor.editText?.setText(color)


    }

    private fun isConnected(profile:OwnerProfile): Boolean {

        val connectivityManager: ConnectivityManager = profile.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiCon: NetworkInfo? = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobCon: NetworkInfo? = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return (wifiCon!=null && wifiCon.isConnected()) || (mobCon!=null && mobCon.isConnected())
    }

    private fun showCustomDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@OwnerProfile)
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
