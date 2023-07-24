package com.example.storyapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {

    private lateinit var preference: PreferenceManager
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        preference = PreferenceManager(this)

        if (preference.getSessionUserId() != null && preference.getSessionToken() != null) {
            navigateToMainActivity()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                navigateToLoginActivity()
            }, SPLASH_DELAY_MS)
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    companion object {
        private const val SPLASH_DELAY_MS = 4000L
    }
}

