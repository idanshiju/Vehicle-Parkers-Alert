package com.example.vpa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import com.google.android.material.textfield.TextInputLayout

class InformerSignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_informer_sign_up)

        val informerName: TextInputLayout = findViewById(R.id.informer_name)
        val informerContno: TextInputLayout = findViewById(R.id.informer_contno)
        val informerGo: Button =findViewById(R.id.informer_go)

        informerGo.setOnClickListener {
            // onClick logic here
        }
    }
}