package com.example.nammakelsa.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammakelsa.model.Worker
import com.example.nammakelsa.repository.WorkerRepository
import com.example.nammakelsa.utils.Constants
import com.example.nammakelsa.utils.GeminiHelper
import com.example.nammakelsa.utils.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkerViewModel : ViewModel() {
    private val repository = WorkerRepository()

    private val _availableWorkers = MutableStateFlow<List<Worker>>(emptyList())
    val availableWorkers = _availableWorkers.asStateFlow()

    private val _filteredWorkers = MutableStateFlow<List<Worker>>(emptyList())
    val filteredWorkers = _filteredWorkers.asStateFlow()

    private val _workerDistances = MutableStateFlow<Map<String, Double?>>(emptyMap())
    val workerDistances = _workerDistances.asStateFlow()

    private val _selectedSkill = MutableStateFlow<String?>(null)
    val selectedSkill = _selectedSkill.asStateFlow()

    private val _selectedMaxDistance = MutableStateFlow(Double.MAX_VALUE)
    val selectedMaxDistance = _selectedMaxDistance.asStateFlow()

    private var searchQuery: String = ""
    private var customerLat: Double = 0.0
    private var customerLng: Double = 0.0
    private var locationAvailable: Boolean = false

    private val _currentWorker = MutableStateFlow<Worker?>(null)
    val currentWorker = _currentWorker.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    private val _suggestedSkill = MutableStateFlow<String?>(null)
    val suggestedSkill = _suggestedSkill.asStateFlow()

    private val _generatedBio = MutableStateFlow<String?>(null)
    val generatedBio = _generatedBio.asStateFlow()

    fun listenToAvailableWorkers() {
        repository.listenToAvailableWorkers { workers ->
            _availableWorkers.value = workers
            applyFilters()
        }
    }

    fun updateCustomerLocation(lat: Double, lng: Double) {
        customerLat = lat
        customerLng = lng
        locationAvailable = true
        applyFilters()
    }

    fun setSkillFilter(skill: String?) {
        _selectedSkill.value = skill
        applyFilters()
    }

    fun setDistanceFilter(maxDistance: Double) {
        _selectedMaxDistance.value = maxDistance
        applyFilters()
    }

    fun setSearchQuery(query: String) {
        searchQuery = query
        applyFilters()
    }

    private fun applyFilters() {
        viewModelScope.launch {
            val allWorkers = _availableWorkers.value
            
            val filtered = allWorkers.filter { worker ->
                val matchesSkill = _selectedSkill.value == null || worker.skillType == _selectedSkill.value
                val matchesSearch = if (searchQuery.isNotEmpty()) {
                    worker.name.contains(searchQuery, ignoreCase = true) ||
                            worker.skillType.contains(searchQuery, ignoreCase = true) ||
                            worker.locationName.contains(searchQuery, ignoreCase = true)
                } else true
                val matchesDistance = if (locationAvailable && _selectedMaxDistance.value != Double.MAX_VALUE) {
                    if (worker.latitude == 0.0 && worker.longitude == 0.0) false
                    else {
                        val dist = LocationHelper.distanceInKm(
                            customerLat, customerLng, worker.latitude, worker.longitude
                        )
                        dist <= _selectedMaxDistance.value
                    }
                } else true
                matchesSkill && matchesSearch && matchesDistance
            }.let { list ->
                if (locationAvailable) {
                    list.sortedBy { worker ->
                        if (worker.latitude == 0.0 && worker.longitude == 0.0) Double.MAX_VALUE
                        else LocationHelper.distanceInKm(
                            customerLat, customerLng, worker.latitude, worker.longitude
                        )
                    }
                } else list
            }

            val distances = if (locationAvailable) {
                filtered.associate { worker ->
                    worker.uid to if (worker.latitude == 0.0 && worker.longitude == 0.0) null
                    else LocationHelper.distanceInKm(
                        customerLat, customerLng, worker.latitude, worker.longitude
                    )
                }
            } else emptyMap()

            _filteredWorkers.value = filtered
            _workerDistances.value = distances
        }
    }

    fun loadWorker(uid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _currentWorker.value = repository.getWorker(uid)
            _isLoading.value = false
        }
    }

    fun saveWorkerProfile(
        worker: Worker,
        profilePhotoUri: Uri?,
        workPhotoUris: List<Uri>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                var finalProfileUrl = worker.profilePhotoUrl
                if (profilePhotoUri != null) {
                    finalProfileUrl = repository.uploadPhoto(
                        "${Constants.STORAGE_PROFILES}/${worker.uid}.jpg",
                        profilePhotoUri
                    )
                }

                val finalWorkUrls = worker.workPhotoUrls.toMutableList()
                if (workPhotoUris.isNotEmpty()) {
                    finalWorkUrls.clear()
                    workPhotoUris.forEachIndexed { index, uri ->
                        val url = repository.uploadPhoto(
                            "${Constants.STORAGE_WORK}/${worker.uid}/$index.jpg",
                            uri
                        )
                        finalWorkUrls.add(url)
                    }
                }

                val updatedWorker = worker.copy(
                    profilePhotoUrl = finalProfileUrl,
                    workPhotoUrls = finalWorkUrls
                )
                repository.saveWorker(updatedWorker)
                _currentWorker.value = updatedWorker
                onSuccess()
            } catch (e: Exception) {
                val errorMsg = e.localizedMessage ?: "Unknown error"
                if (errorMsg.contains("Object does not exist")) {
                    _message.value = "Storage Error: Please ensure you have selected a profile photo or wait for the bucket to initialize."
                } else {
                    _message.value = errorMsg
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateAvailability(uid: String, isAvailable: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateAvailability(uid, isAvailable)
            } catch (e: Exception) {
                _message.value = e.localizedMessage
            }
        }
    }

    fun generateBio(name: String, skill: String, rate: Int, location: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val bio = GeminiHelper.generateWorkerBio(name, skill, rate, location)
            _generatedBio.value = bio
            _isLoading.value = false
        }
    }

    fun suggestSkill(description: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val skill = GeminiHelper.suggestSkillsFromDescription(description)
            _suggestedSkill.value = skill
            _isLoading.value = false
        }
    }

    fun clearSuggestions() {
        _suggestedSkill.value = null
        _generatedBio.value = null
    }

    fun clearMessage() {
        _message.value = null
    }
}
