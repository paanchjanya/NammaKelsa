package com.example.nammakelsa.ui.customer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nammakelsa.R
import com.example.nammakelsa.databinding.ActivityAiSuggesterBinding
import com.example.nammakelsa.utils.GeminiHelper
import kotlinx.coroutines.launch

import androidx.activity.viewModels
import com.example.nammakelsa.ui.viewmodel.WorkerViewModel
import kotlinx.coroutines.flow.collectLatest

class AiSkillSuggesterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAiSuggesterBinding
    private val viewModel: WorkerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiSuggesterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.btnSuggestSkill.setOnClickListener {
            val description = binding.etWorkDescription.text.toString().trim()
            if (description.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_description), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.suggestSkill(description)
        }

        observeViewModel()
        setupBottomNav()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.btnSuggestSkill.isEnabled = !isLoading
                binding.btnSuggestSkill.text = if (isLoading) getString(R.string.loading_indicator) 
                                             else getString(R.string.btn_suggest)
                if (isLoading) {
                    binding.tvSuggestedSkill.text = getString(R.string.msg_thinking)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.suggestedSkill.collectLatest { skill ->
                if (skill != null) {
                    binding.tvSuggestedSkill.text = getString(R.string.msg_suggested_skill, skill)
                }
            }
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.selectedItemId = R.id.nav_ai
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_find_work -> {
                    startActivity(Intent(this, CustomerSearchActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_ai -> true
                else -> false
            }
        }
    }
}
