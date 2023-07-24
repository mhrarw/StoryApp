package com.example.storyapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var edEmail: EditText
    private lateinit var edPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var apiService: ApiService
    private lateinit var preference: PreferenceManager
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        apiService = ApiClient.instance.create(ApiService::class.java)
        preference = PreferenceManager(this)

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        loginViewModel.setPreference(preference)

        edEmail = findViewById(R.id.ed_login_email)
        edPassword = findViewById(R.id.ed_login_password)
        btnLogin = findViewById(R.id.btnLogin)

        val tvRegister = findViewById<TextView>(R.id.tv_register)
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        val sessionUserId = preference.getSessionUserId()
        val sessionToken = preference.getSessionToken()
        if (!sessionUserId.isNullOrEmpty() && !sessionToken.isNullOrEmpty()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener {
            val email = edEmail.text.toString().trim()
            val password = edPassword.text.toString().trim()

            loginViewModel.loginUser(email, password)
        }
        observeLoginResult()
    }

    private fun observeLoginResult() {
        loginViewModel.loginResult.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is Result.Error -> {
                    Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
