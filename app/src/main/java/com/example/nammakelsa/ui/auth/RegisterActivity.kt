package com.example.nammakelsa.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.nammakelsa.databinding.ActivityRegisterBinding
import com.example.nammakelsa.ui.worker.WorkerProfileActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Added tvSignIn back navigation
        binding.tvSignIn.setOnClickListener {
            finish() // goes back to login
        }

        binding.btnRegister.setOnClickListener {
            val name     = binding.etName.text.toString().trim()
            val phone    = binding.etPhone.text.toString().trim()
            val email    = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnRegister.isEnabled = false
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    // Pass name & phone to profile screen to pre-fill
                    val intent = Intent(this, WorkerProfileActivity::class.java).apply {
                        putExtra("NAME", name)
                        putExtra("PHONE", phone)
                    }
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(this, "Registration failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}