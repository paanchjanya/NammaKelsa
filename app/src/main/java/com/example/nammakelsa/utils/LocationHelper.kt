package com.example.nammakelsa.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.math.*

object LocationHelper {

    // Calculates distance between two GPS points in KM using the standard Android Location library
    fun distanceInKm(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return (results[0] / 1000.0)
    }

    fun formatDistance(km: Double): String {
        return if (km < 1.0) "${(km * 1000).toInt()}m away"
        else "${"%.1f".format(km)}km away"
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context, onResult: (Location?) -> Unit) {
        val client = LocationServices.getFusedLocationProviderClient(context)
        val cancellationToken = CancellationTokenSource()

        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken.token)
            .addOnSuccessListener { location -> onResult(location) }
            .addOnFailureListener { onResult(null) }
    }
}