package com.example.vpa

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_login)

        val image: ImageView
        val text1: TextView
        val text2: TextView
        val callOwnerSignUp: Button
        val callInformerSignUp: Button

        //Hooks
        callOwnerSignUp= findViewById(R.id.owner_button)
        callInformerSignUp=findViewById(R.id.informer_button)
        image=findViewById(R.id.login_image)
        text1=findViewById(R.id.login_name)
        text2=findViewById(R.id.slogan_name)

        callOwnerSignUp.setOnClickListener {
            val intent = Intent(this@Login, OwnerSignUp::class.java)
            val pairs = arrayOf(
                Pair<View, String>(image, "logo_image"),
                Pair<View, String>(text1, "logo_text"),
                Pair<View, String>(text2, "logo_text2"),
                Pair<View, String>(callOwnerSignUp, "owner_signup_trans"),
                Pair<View, String>(callInformerSignUp, "info_signup_trans"),
            )
            //wrap the call in API level 21 or higher
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                val options = ActivityOptions.makeSceneTransitionAnimation(this, *pairs)
                startActivity(intent, options.toBundle())
            }
        }

        callInformerSignUp.setOnClickListener {
            val intent = Intent(this@Login, InformerSignUp::class.java)
            val pairs = arrayOf(
                Pair<View, String>(image, "logo_image"),
                Pair<View, String>(text1, "logo_text"),
                Pair<View, String>(text2, "logo_text2"),
                Pair<View, String>(callOwnerSignUp, "owner_signup_trans"),
                Pair<View, String>(callInformerSignUp, "info_signup_trans"),
            )
            //wrap the call in API level 21 or higher
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                val options = ActivityOptions.makeSceneTransitionAnimation(this, *pairs)
                startActivity(intent, options.toBundle())
            }
        }
    }
}