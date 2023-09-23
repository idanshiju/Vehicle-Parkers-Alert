package com.example.vpa

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import android.telephony.SmsManager
import android.Manifest;
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.provider.Settings
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.database.*

class DetailActivity : AppCompatActivity() {
    private val permissionRequestCode = 1

    private lateinit var detailName: TextView
    private lateinit var detailModel: TextView
    private lateinit var alertButton: Button
    private lateinit var detailVehicleNo: TextView
    private var key: String = ""
    var TOKEN: String=""
    var counter: Int=0
    var Nam:String=""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_detail)

        detailName = findViewById(R.id.detailName)
        detailModel = findViewById(R.id.detailModel)
        alertButton = findViewById(R.id.alert_button)
        detailVehicleNo = findViewById(R.id.detailVehicleNo)

        val bundle = intent.extras
        if (bundle != null) {
            detailName.text = bundle.getString("name")
            detailModel.text = bundle.getString("vehicle")
            detailVehicleNo.text = bundle.getString("vehNo")
            TOKEN = bundle.getString("token").toString()
            key = bundle.getString("Key", "")
            Nam = bundle.getString("name").toString()
        }

        alertButton.setOnClickListener {
            if (!isConnected(this)) {
                showCustomDialog()
            }

            else
            {
                val title = "Alert"
                val message =
                    "Dear Car Owner, your car is causing an obstruction. Please move it. Thank you."
                Send.pushNotification(
                    context = this@DetailActivity,
                    TOKEN,
                    title,
                    message
                ) { success, resultMessage ->
                    if (success) {
                        Toast.makeText(
                            this@DetailActivity,
                            "Notification sent successfully.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val rootNode: FirebaseDatabase = FirebaseDatabase.getInstance()
                        val reference: DatabaseReference = rootNode.getReference("owners")

// Query the database to find the specific owner based on the name
                        val query: Query = reference.orderByChild("name").equalTo(Nam)
                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (snapshot in dataSnapshot.children) {
                                        val contact = snapshot.key // Get the contact value
                                        val owner = snapshot.getValue(OwnerHelperClass::class.java)
                                        val notify = owner?.notify
                                        if (notify != null) {
                                            counter = notify + 1
                                        }

                                        // Update the notify field
                                        val updateData: HashMap<String, Any> = hashMapOf(
                                            "notify" to counter
                                        )
                                        if (contact != null) {
                                            reference.child(contact).updateChildren(updateData)
                                                .addOnSuccessListener {
                                                    // Notify update successful
                                                }
                                                .addOnFailureListener { exception ->
                                                    // Handle update failure
                                                }
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Handle database error
                            }
                        })

                    } else {
                        Toast.makeText(
                            this@DetailActivity,
                            "Notification failed: $resultMessage",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
    private fun isConnected(detail: DetailActivity): Boolean {

        val connectivityManager: ConnectivityManager = detail.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiCon: NetworkInfo? = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobCon: NetworkInfo? = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return (wifiCon!=null && wifiCon.isConnected()) || (mobCon!=null && mobCon.isConnected())
    }

    private fun showCustomDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@DetailActivity)
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