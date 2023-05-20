package com.example.vpa

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class OwnerSignUp : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_owner_sign_up)

        var rootNode: FirebaseDatabase
        var reference: DatabaseReference

        val ownerName: TextInputLayout = findViewById(R.id.owner_name)
        val ownerContno: TextInputLayout = findViewById(R.id.owner_contno)
        val ownerVehno: TextInputLayout = findViewById(R.id.owner_vehno)
        val ownerVehcolor: TextInputLayout = findViewById(R.id.owner_vehcolor)
        val ownerGo: Button = findViewById(R.id.owner_go)

        fun validateOName(): Boolean {
            val value = ownerName.editText?.text.toString()
            if (value.isEmpty()) {
                ownerName.error = "Field cannot be empty"
                return false
            } else {
                ownerName.error = null
                ownerName.isErrorEnabled = false
                return true
            }
        }

        fun validateOContact(): Boolean {
            val value = ownerContno.editText?.text.toString()
            val noWhiteSpace = "\\A\\w{4,20}\\z"
            if (value.isEmpty()) {
                ownerContno.error = "Field cannot be empty"
                return false
            }else if (!value.matches(Regex((noWhiteSpace)))) {
                ownerContno.error = "White Spaces are not allowed"
                return false
            }else if(value.length!=10) {
                ownerContno.error = "Invalid number!"
                return false
            }
            else {
                ownerContno.error = null
                ownerContno.isErrorEnabled = false
                return true
            }
        }

        fun validateVehno(): Boolean {
            val value = ownerVehno.editText?.text.toString()
            val regexPattern = "[A-Z]{2} \\d{1,2}[A-Z]{1,2} \\d{4}"
            if (value.isEmpty()) {
                ownerVehno.error = "Field cannot be empty"
                return false
            } else if (!value.matches(Regex((regexPattern)))) {
                ownerVehno.error = "Invalid Pattern! Follow : AB 12CD 3456"
                return false
            } else {
                ownerVehno.error = null
                ownerVehno.isErrorEnabled = false
                return true
            }
        }

        fun validateColor(): Boolean {
            val value = ownerVehcolor.editText?.text.toString()
            if (value.isEmpty()) {
                ownerVehcolor.error = "Field cannot be empty"
                return false
            } else {
                ownerVehcolor.error = null
                ownerVehcolor.isErrorEnabled = false
                return true
            }
        }

        ownerGo.setOnClickListener {
            val isnameValidated = validateOName()
            val iscontactValidated = validateOContact()
            val isnoValidated = validateVehno()
            val iscolorValidated = validateColor()
            if (!isnameValidated || !iscontactValidated || !isnoValidated || !iscolorValidated) {
                return@setOnClickListener
            }

            rootNode = FirebaseDatabase.getInstance()
            reference = rootNode.getReference("owners")

            // Get all the values
            val enteredName = ownerName.editText?.text.toString().trim()
            val enteredContact = ownerContno.editText?.text.toString()
            val enteredVehNo = ownerVehno.editText?.text.toString().trim()
            val enteredVehColor = ownerVehcolor.editText?.text.toString().trim()

            val ohelperClass = OwnerHelperClass(enteredName, enteredContact, enteredVehNo, enteredVehColor)
            reference.child(enteredContact).setValue(ohelperClass)

            val intent = Intent(applicationContext, OwnerProfile::class.java)
            intent.putExtra("name", enteredName)
            intent.putExtra("contact", enteredContact)
            intent.putExtra("vehNo", enteredVehNo)
            intent.putExtra("vehColor", enteredVehColor)
            startActivity(intent)
        }
    }
}