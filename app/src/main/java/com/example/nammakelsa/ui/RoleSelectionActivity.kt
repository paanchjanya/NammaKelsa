package com.example.nammakelsa.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.nammakelsa.databinding.ActivityRoleSelectionBinding
import com.example.nammakelsa.ui.auth.LoginActivity
import com.example.nammakelsa.ui.customer.CustomerSearchActivity

class RoleSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoleSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoleSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Always sign out when landing here so each worker logs in fresh
        FirebaseAuth.getInstance().signOut()

        binding.btnWorker.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnCustomer.setOnClickListener {
            startActivity(Intent(this, CustomerSearchActivity::class.java))
        }
    }
}