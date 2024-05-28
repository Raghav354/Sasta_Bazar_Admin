package com.example.shoppingappadmin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.shoppingappadmin.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

        checkRememberedCredentials()

        binding.loginbutton.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            loginUser(email, password)
        }
        binding.register.setOnClickListener {
            startActivity(Intent(this@LoginActivity , SignupActivity::class.java))
            finish()
        }

        binding.forgotpassword.setOnClickListener {
            resetPassword(binding.email.text.toString())
        }

        binding.rememberMeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val email = binding.email.text.toString()
                val password = binding.password.text.toString()
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    saveCredentials(email, password)
                    Toast.makeText(this, "Credentials saved", Toast.LENGTH_SHORT).show()
                } else {
                    clearCredentials()
                    binding.rememberMeCheckbox.isChecked = false
                    Toast.makeText(this, "Please provide email and password", Toast.LENGTH_SHORT).show()
                }
            } else {
                clearCredentials()
                Toast.makeText(this, "Credentials cleared", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        Toast.makeText(baseContext, "Login successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(baseContext, "Please verify your email address.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(baseContext, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun resetPassword(email: String) {
        if (email.isNotEmpty()) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Password reset email sent to $email.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Error sending password reset email: ${task.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(this, "Please provide the mail...", Toast.LENGTH_SHORT).show()
        }

    }

    private fun checkRememberedCredentials() {
        val rememberedEmail = sharedPreferences.getString("email", "")
        val rememberedPassword = sharedPreferences.getString("password", "")
        if (!rememberedEmail.isNullOrEmpty() && !rememberedPassword.isNullOrEmpty()) {
            binding.email.setText(rememberedEmail)
            binding.password.setText(rememberedPassword)
            binding.rememberMeCheckbox.isChecked = true
        }
    }

    private fun saveCredentials(email: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

    private fun clearCredentials() {
        val editor = sharedPreferences.edit()
        editor.remove("email")
        editor.remove("password")
        editor.apply()
    }
}