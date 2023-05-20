package com.example.vpa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class InformerSignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_informer_sign_up)

        var rootNode:FirebaseDatabase
        var reference:DatabaseReference

        val informerName: TextInputLayout = findViewById(R.id.informer_name)
        val informerContno: TextInputLayout = findViewById(R.id.informer_contno)
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
                return false;
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
            val isnameValidated = validateIName()
            val iscontactValidated = validateIContact()
            if (!isnameValidated || !iscontactValidated) {
                return@setOnClickListener
            }

            rootNode = FirebaseDatabase.getInstance()
            reference = rootNode.getReference("informers")

            // Get all the values
            val name = informerName.editText?.text.toString()
            val contact = informerContno.editText?.text.toString()

            val ihelperClass = InformerHelperClass(name, contact)
            reference.child(contact).setValue(ihelperClass)

        }
    }
}