package com.example.vpa

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import com.google.android.material.textfield.TextInputLayout

class OwnerSignUp : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_owner_sign_up)

        val ownerName: TextInputLayout = findViewById(R.id.owner_name)
        val ownerContno: TextInputLayout = findViewById(R.id.owner_contno)
        val ownerVehno: TextInputLayout = findViewById(R.id.owner_vehno)
        val ownerVehcolor: TextInputLayout = findViewById(R.id.owner_vehcolor)
        val ownerGo: Button =findViewById(R.id.owner_go)

        ownerGo.setOnClickListener {
            // onClick logic here
        }
    }
}