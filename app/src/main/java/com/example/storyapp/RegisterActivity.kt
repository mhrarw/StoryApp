package com.example.storyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.response.RegisterResponse
import com.example.storyapp.viewmodel.RegisterViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var edNameRegister: EditText
    private lateinit var edEmailRegister: EditText
    private lateinit var edPasswordRegister: EditText
    private lateinit var btnRegister: Button
    private lateinit var apiService: ApiService
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        apiService = ApiClient.instance.create(ApiService::class.java)
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        edNameRegister = findViewById(R.id.ed_register_name)
        edEmailRegister = findViewById(R.id.ed_register_email)
        edPasswordRegister = findViewById(R.id.ed_register_password)
        btnRegister = findViewById(R.id.btnRegister)

        val tvSign = findViewById<TextView>(R.id.tv_sign)
        tvSign.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnRegister.setOnClickListener {
            val name = edNameRegister.text.toString().trim()
            val email = edEmailRegister.text.toString().trim()
            val password = edPasswordRegister.text.toString().trim()

            registerViewModel.registerUser(name, email, password)
        }
        observeRegisterResult()
    }

    private fun observeRegisterResult() {
        registerViewModel.registerResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is Result.Error -> {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}