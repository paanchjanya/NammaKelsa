package com.example.nammakelsa.ui.customer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nammakelsa.R
import com.example.nammakelsa.databinding.ActivityCustomerSearchBinding
import com.example.nammakelsa.ui.viewmodel.WorkerViewModel
import com.example.nammakelsa.utils.Constants
import com.example.nammakelsa.utils.LocationHelper
import com.google.android.material.chip.Chip
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CustomerSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerSearchBinding
    private val viewModel: WorkerViewModel by viewModels()
    private lateinit var adapter: WorkerAdapter

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) fetchCustomerLocation()
            else {
                Toast.makeText(this, getString(R.string.msg_distance_unavailable_no_location), Toast.LENGTH_SHORT).show()
                viewModel.setDistanceFilter(Double.MAX_VALUE)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupFilters()
        setupSearch()
        setupBottomNav()
        
        requestLocationPermission()
        viewModel.listenToAvailableWorkers()

        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = WorkerAdapter { worker -> callWorker(worker.phone) }
        binding.rvWorkers.layoutManager = LinearLayoutManager(this)
        binding.rvWorkers.adapter = adapter
    }

    private fun setupFilters() {
        // Skill chips
        addChip(binding.chipGroupFilter, getString(R.string.skill_all), null)
        Constants.SKILLS.forEach { skill ->
            addChip(binding.chipGroupFilter, skill, skill)
        }
        binding.chipGroupFilter.check(binding.chipGroupFilter.getChildAt(0).id)

        // Distance chips
        addDistanceChip(getString(R.string.distance_any), Double.MAX_VALUE)
        addDistanceChip(getString(R.string.distance_unit_km, "2"), 2.0)
        addDistanceChip(getString(R.string.distance_unit_km, "5"), 5.0)
        addDistanceChip(getString(R.string.distance_unit_km, "10"), 10.0)
        binding.chipGroupDistance.check(binding.chipGroupDistance.getChildAt(0).id)
    }

    private fun addChip(group: com.google.android.material.chip.ChipGroup, text: String, skill: String?) {
        val chip = layoutInflater.inflate(R.layout.layout_filter_chip, group, false) as Chip
        chip.text = text
        chip.id = View.generateViewId()
        chip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.setSkillFilter(skill)
        }
        group.addView(chip)
    }

    private fun addDistanceChip(text: String, distance: Double) {
        val chip = layoutInflater.inflate(R.layout.layout_filter_chip, binding.chipGroupDistance, false) as Chip
        chip.text = text
        chip.id = View.generateViewId()
        chip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.setDistanceFilter(distance)
        }
        binding.chipGroupDistance.addView(chip)
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupBottomNav() {
        binding.bottomNav.selectedItemId = R.id.nav_find_work
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_find_work -> true
                R.id.nav_ai -> {
                    startActivity(Intent(this, AiSkillSuggesterActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            combine(
                viewModel.filteredWorkers,
                viewModel.workerDistances,
                viewModel.selectedSkill,
                viewModel.selectedMaxDistance
            ) { workers, distances, skill, distance ->
                val skillText = skill ?: getString(R.string.skill_all)
                val distText = if (distance == Double.MAX_VALUE) getString(R.string.distance_any) 
                              else getString(R.string.distance_unit_km, distance.toInt().toString())
                
                Triple(workers, distances, getString(R.string.msg_result_count, workers.size, skillText, distText))
            }.collect { (workers, distances, resultText) ->
                adapter.updateList(workers, distances)
                binding.tvResultCount.text = resultText
                
                if (workers.isEmpty()) {
                    binding.rvWorkers.visibility = View.GONE
                    binding.layoutEmpty.visibility = View.VISIBLE
                } else {
                    binding.rvWorkers.visibility = View.VISIBLE
                    binding.layoutEmpty.visibility = View.GONE
                }
            }
        }
    }

    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED -> fetchCustomerLocation()
            else -> locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchCustomerLocation() {
        LocationHelper.getCurrentLocation(this) { location ->
            if (location != null) {
                viewModel.updateCustomerLocation(location.latitude, location.longitude)
            }
        }
    }

    private fun callWorker(phone: String) {
        if (phone.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_no_phone), Toast.LENGTH_SHORT).show()
            return
        }
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
    }
}
