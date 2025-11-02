package com.example.contactapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)

        login.setOnClickListener {
            if (username.text.toString() == "Admin" && password.text.toString() == "Admin") {
                ToastHelper.showCustomToast(this, "Login successful!", ToastHelper.ToastType.SUCCESS)
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USERNAME", username.text.toString())
                startActivity(intent)
                finish()
            } else {
                ToastHelper.showCustomToast(this, "Invalid credentials", ToastHelper.ToastType.ERROR)
            }
        }
    }
}