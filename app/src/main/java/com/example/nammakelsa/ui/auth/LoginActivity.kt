package com.example.nammakelsa.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.nammakelsa.databinding.ActivityLoginBinding
import com.example.nammakelsa.ui.worker.WorkerProfileActivity
import com.example.nammakelsa.utils.Constants

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val email    = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showTopToast("Please fill all fields")
                return@setOnClickListener
            }

            binding.btnLogin.isEnabled = false
            binding.btnLogin.text = "Checking..."

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: return@addOnSuccessListener

                    // Check if this worker has a profile in Firestore
                    db.collection(Constants.COLLECTION_WORKERS).document(uid).get()
                        .addOnSuccessListener { doc ->
                            if (doc.exists()) {
                                // Existing worker → load their profile
                                goToProfile(isNewWorker = false)
                            } else {
                                // Logged in via Auth but no Firestore profile yet
                                // This means they registered but never completed profile
                                goToProfile(isNewWorker = true)
                            }
                        }
                        .addOnFailureListener {
                            resetButton()
                            showTopToast("Error checking profile")
                        }
                }
                .addOnFailureListener { exception ->
                    resetButton()
                    val errorMsg = when {
                        exception.message?.contains("no user record") == true ||
                                exception.message?.contains("user-not-found") == true ->
                            "No worker found with this email. Please register first."

                        exception.message?.contains("password is invalid") == true ||
                                exception.message?.contains("wrong-password") == true ->
                            "Incorrect password. Please try again."

                        exception.message?.contains("badly formatted") == true ->
                            "Please enter a valid email address."

                        else -> "Login failed. Please check your credentials."
                    }
                    showTopToast(errorMsg)
                }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun goToProfile(isNewWorker: Boolean) {
        val intent = Intent(this, WorkerProfileActivity::class.java).apply {
            putExtra("IS_NEW_WORKER", isNewWorker)
        }
        startActivity(intent)
        finish()
    }

    private fun resetButton() {
        binding.btnLogin.isEnabled = true
        binding.btnLogin.text = "Login"
    }

    private fun showTopToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        toast.setGravity(android.view.Gravity.TOP or android.view.Gravity.CENTER_HORIZONTAL, 0, 160)
        toast.show()
    }
}