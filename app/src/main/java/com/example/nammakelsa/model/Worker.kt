package com.example.nammakelsa.model

data class Worker(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val skillType: String = "",
    val dailyRate: Int = 0,
    val locationName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val profilePhotoUrl: String = "",
    val workPhotoUrls: List<String> = emptyList(),
    val isAvailable: Boolean = false
)