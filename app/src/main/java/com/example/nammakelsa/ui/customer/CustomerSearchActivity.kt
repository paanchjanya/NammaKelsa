package com.example.nammakelsa.ui.customer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import com.example.nammakelsa.R
import com.example.nammakelsa.ui.viewmodel.WorkerViewModel
import com.example.nammakelsa.utils.Constants
import com.example.nammakelsa.utils.LocationHelper

class CustomerSearchActivity : AppCompatActivity() {

    private val viewModel: WorkerViewModel by viewModels()

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
        
        requestLocationPermission()
        viewModel.listenToAvailableWorkers()

        setContent {
            val workers by viewModel.filteredWorkers.collectAsState()
            
            SearchFeedScreen(
                workers = workers,
                skills = Constants.SKILLS,
                selectedSkill = null, // Logic to be linked if needed
                onSkillSelected = { viewModel.setSkillFilter(it) },
                onWorkerClick = { callWorker(it.phone) }
            )
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
