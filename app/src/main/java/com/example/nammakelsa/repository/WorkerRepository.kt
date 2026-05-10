package com.example.nammakelsa.repository

import android.net.Uri
import com.example.nammakelsa.model.Worker
import com.example.nammakelsa.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class WorkerRepository {
    private val db = FirebaseFirestore.getInstance()
    // Explicitly using the bucket URL from your updated google-services.json
    private val storage = FirebaseStorage.getInstance("gs://nammakelsa-80c01.firebasestorage.app")
    private val workersCollection = db.collection(Constants.COLLECTION_WORKERS)

    fun listenToAvailableWorkers(onResult: (List<Worker>) -> Unit): ListenerRegistration {
        return workersCollection
            .whereEqualTo("isAvailable", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val workers = snapshot?.documents?.mapNotNull { it.toObject(Worker::class.java) } ?: emptyList()
                onResult(workers)
            }
    }

    suspend fun getWorker(uid: String): Worker? {
        return try {
            workersCollection.document(uid).get().await().toObject(Worker::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveWorker(worker: Worker) {
        workersCollection.document(worker.uid).set(worker).await()
    }

    suspend fun updateAvailability(uid: String, isAvailable: Boolean) {
        // Use set with merge instead of update to handle new users who don't have a document yet
        workersCollection.document(uid)
            .set(mapOf("isAvailable" to isAvailable), SetOptions.merge())
            .await()
    }

    suspend fun uploadPhoto(path: String, uri: Uri): String {
        val ref = storage.reference.child(path)
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }
}
