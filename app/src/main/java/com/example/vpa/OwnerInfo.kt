package com.example.vpa

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
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList


class OwnerInfo : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var databaseReference: DatabaseReference
    private lateinit var eventListener: ValueEventListener
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: MutableList<DataClass>
    private lateinit var adapter: MyAdapter
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_owner_info)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.search)
        searchView.clearFocus()
        showAll()

        val gridLayoutManager = GridLayoutManager(this, 1)
        recyclerView.layoutManager = gridLayoutManager

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        val dialog: AlertDialog = builder.create()
        dialog.show()

        dataList = mutableListOf()
        adapter = MyAdapter(this, dataList)
        recyclerView.adapter = adapter

        databaseReference = FirebaseDatabase.getInstance().getReference("owners")
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val informersRef: DatabaseReference = database.getReference("informers")
        val informerId = sharedPreferences.getString("icontact", "")
        val editor = sharedPreferences.edit()
        editor.putBoolean("isiLoggedIn", true)
        editor.apply()
        dialog.show()
        val oDelete: ImageView = findViewById(R.id.button_delete)
        oDelete.setOnClickListener {
            if (!isConnected(this)) {
                showCustomDialog()
            } else {
                if (!informerId.isNullOrEmpty()) {
                    informersRef.child(informerId).removeValue()
                        .addOnSuccessListener {
                            // Informer successfully removed from the database
                            // Navigate back to the Login activity
                            editor.putBoolean("isiLoggedIn", false)
                            editor.apply()
                            val intent = Intent(this, Login::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@OwnerInfo,
                                "Failed to Delete.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
        }

        eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(DataClass::class.java)
                    dataClass?.let {
                        it.key = itemSnapshot.key.toString()
                        dataList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }
        }

        databaseReference.addValueEventListener(eventListener)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        })
    }
    private fun showAll() {
        val fullnameLabel: TextView = findViewById(R.id.fullname_field)

        val fullname=sharedPreferences.getString("iname","")

        fullnameLabel.setText(fullname)
    }
    override fun onBackPressed() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isiLoggedIn", true)
        editor.apply()
        super.onBackPressed()
    }

    private fun searchList(text: String) {
        val searchList = ArrayList<DataClass>()
        for (dataClass in dataList) {
            val vehicleMatch = dataClass.vehicle?.toLowerCase(Locale.ROOT)?.contains(text.toLowerCase(Locale.ROOT)) == true
            val nameMatch = dataClass.name?.toLowerCase(Locale.ROOT)?.contains(text.toLowerCase(Locale.ROOT)) == true
            val vehNoMatch = dataClass.vehNo?.toLowerCase(Locale.ROOT)?.contains(text.toLowerCase(Locale.ROOT)) == true
            if (vehicleMatch||nameMatch||vehNoMatch) {
                searchList.add(dataClass)
            }
        }
        adapter.searchDataList(searchList)
    }

    override fun onDestroy() {
        super.onDestroy()
        databaseReference.removeEventListener(eventListener)
    }
    private fun isConnected(info:OwnerInfo): Boolean {

        val connectivityManager: ConnectivityManager = info.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiCon: NetworkInfo? = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobCon: NetworkInfo? = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return (wifiCon!=null && wifiCon.isConnected()) || (mobCon!=null && mobCon.isConnected())
    }

    private fun showCustomDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@OwnerInfo)
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