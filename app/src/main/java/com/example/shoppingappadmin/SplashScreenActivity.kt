package com.example.shoppingappadmin

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.airbnb.lottie.LottieAnimationView
import com.example.shoppingappadmin.databinding.ActivitySplashScreenBinding
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var binding: ActivitySplashScreenBinding
    private val TAG = "SplashScreenActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        lottieAnimationView = binding.lottieAnimationView
        Handler(Looper.getMainLooper()).postDelayed({
            val user = auth.currentUser
            if (user == null || !user.isEmailVerified) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                finish()
            }
        }, 1200)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: Playing animation")
        binding.lottieAnimationView.playAnimation()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: Cancelling animation")
        binding.lottieAnimationView.cancelAnimation()
    }
}