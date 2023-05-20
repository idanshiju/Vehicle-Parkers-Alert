package com.example.vpa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout

class OwnerProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_owner_profile)

        showAllDAta()
    }

    private fun showAllDAta() {
        val oFullname: TextInputLayout = findViewById(R.id.fullname_profile)
        val oContno: TextInputLayout = findViewById(R.id.contactNo_profile)
        val oVehno: TextInputLayout = findViewById(R.id.vehNo_profile)
        val oVehcolor: TextInputLayout = findViewById(R.id.vehColor_profile)
        val oUpdate: Button = findViewById(R.id.button_update)
        val fullnameLabel: TextView = findViewById(R.id.fullname_field)

        val intent = getIntent()
        val fullname=intent.getStringExtra("name")
        val contact=intent.getStringExtra("contact")
        val vehicleNo=intent.getStringExtra("vehNo")
        val color=intent.getStringExtra("vehColor")

        fullnameLabel.setText(fullname)
        oFullname.editText?.setText(fullname)
        oContno.editText?.setText(contact)
        oVehno.editText?.setText(vehicleNo)
        oVehcolor.editText?.setText(color)
    }
}