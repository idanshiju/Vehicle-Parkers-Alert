package com.example.vpa

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.util.Pair

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)

        val SPLASH_SCREEN = 3500

        val topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        val bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        // Hooks
        val image: ImageView = findViewById(R.id.imageView1)
        val logo: TextView = findViewById(R.id.textView1)
        val slogan: TextView = findViewById(R.id.textView2)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

// Set animation to elements
        image.startAnimation(topAnim)
        logo.startAnimation(bottomAnim)
        slogan.startAnimation(bottomAnim)

        Handler().postDelayed({
            //Call next screen
            val intent = Intent(this@MainActivity, Login::class.java)
            // Check if app is being opened for the first time or not


            // Attach all the elements those you want to animate in design
            val pairs = arrayOf(
                Pair<View, String>(image, "logo_image"),
                Pair<View, String>(logo, "logo_text")
            )

            //wrap the call in API level 21 or higher
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                val options = ActivityOptions.makeSceneTransitionAnimation(this, *pairs)
                startActivity(intent, options.toBundle())
                finish()
            }
        }, SPLASH_SCREEN.toLong())

    }
}