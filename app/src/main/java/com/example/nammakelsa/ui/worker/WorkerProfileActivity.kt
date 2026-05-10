package com.example.nammakelsa.ui.worker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.example.nammakelsa.databinding.ActivityWorkerProfileBinding
import com.example.nammakelsa.model.Worker
import com.example.nammakelsa.utils.Constants
import com.example.nammakelsa.utils.GeminiHelper
import com.example.nammakelsa.utils.LocationHelper
import kotlinx.coroutines.launch

class WorkerProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkerProfileBinding
    private val auth    = FirebaseAuth.getInstance()
    private val db      = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private var profilePhotoUri: Uri? = null
    private val workPhotoUris = mutableListOf<Uri>()

    // ── Photo pickers ──────────────────────────────────────────────
    private val profilePhotoPicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                // Persist URI permission so it survives location dialog
                contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                profilePhotoUri = it
                Glide.with(this).load(it).circleCrop().into(binding.ivProfilePhoto)
            }
        }

    private val workPhotosPicker =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                workPhotoUris.clear()
                uris.take(3).forEach { uri ->
                    try {
                        contentResolver.takePersistableUriPermission(
                            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (_: Exception) {}
                    workPhotoUris.add(uri)
                }
                val ivList = listOf(binding.ivWork1, binding.ivWork2, binding.ivWork3)
                workPhotoUris.forEachIndexed { i, uri ->
                    Glide.with(this).load(uri).centerCrop().into(ivList[i])
                }
            }
        }

    // ── Location permission ────────────────────────────────────────
    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) fetchLocationThenSave()
            else proceedWithSave(0.0, 0.0)  // Save without location
        }

    // ──────────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSkillChips()

        val isNewWorker = intent.getBooleanExtra("IS_NEW_WORKER", false)
        if (isNewWorker) {
            Toast.makeText(this, "Welcome! Please complete your profile.", Toast.LENGTH_LONG).show()
        } else {
            loadExistingProfile()
        }

        intent.getStringExtra("NAME")?.let  { binding.etName.setText(it) }
        intent.getStringExtra("PHONE")?.let { binding.etPhone.setText(it) }

        binding.btnPickProfilePhoto.setOnClickListener {
            profilePhotoPicker.launch("image/*")
        }
        binding.btnPickWorkPhotos.setOnClickListener {
            workPhotosPicker.launch("image/*")
        }
        binding.btnSaveProfile.setOnClickListener { saveProfile() }
        binding.btnGenerateBio.setOnClickListener  { generateBioWithAI() }
        binding.btnSuggestSkill.setOnClickListener { suggestSkillWithAI() }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val i = Intent(this, com.example.nammakelsa.ui.RoleSelectionActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }

        listenToAvailability()
    }

    // ── Skill chips ────────────────────────────────────────────────
    private fun setupSkillChips() {
        Constants.SKILLS.forEach { skill ->
            val chip = Chip(this).apply {
                text = skill
                isCheckable = true
                isClickable  = true
                chipBackgroundColor =
                    ContextCompat.getColorStateList(this@WorkerProfileActivity,
                        com.example.nammakelsa.R.color.chip_background_color)
                setTextColor(ContextCompat.getColorStateList(this@WorkerProfileActivity,
                    com.example.nammakelsa.R.color.chip_text_color))
                chipStrokeColor =
                    ContextCompat.getColorStateList(this@WorkerProfileActivity,
                        com.example.nammakelsa.R.color.chip_stroke_color)
                chipStrokeWidth = 1.5f
            }
            binding.chipGroupSkills.addView(chip)
        }
    }

    // ── Load existing profile ──────────────────────────────────────
    private fun loadExistingProfile() {
        val uid = auth.currentUser?.uid ?: return
        db.collection(Constants.COLLECTION_WORKERS).document(uid).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) return@addOnSuccessListener
                val worker = doc.toObject(Worker::class.java) ?: return@addOnSuccessListener
                binding.etName.setText(worker.name)
                binding.etPhone.setText(worker.phone)
                binding.etDailyRate.setText(worker.dailyRate.toString())
                binding.etLocation.setText(worker.locationName)
                for (i in 0 until binding.chipGroupSkills.childCount) {
                    val chip = binding.chipGroupSkills.getChildAt(i) as Chip
                    if (chip.text == worker.skillType) { chip.isChecked = true; break }
                }
                if (worker.profilePhotoUrl.isNotEmpty())
                    Glide.with(this).load(worker.profilePhotoUrl)
                        .circleCrop().into(binding.ivProfilePhoto)
            }
    }

    // ── Availability ───────────────────────────────────────────────
    private fun listenToAvailability() {
        val uid = auth.currentUser?.uid ?: return
        db.collection(Constants.COLLECTION_WORKERS).document(uid)
            .addSnapshotListener { snap, _ ->
                val isAvailable = snap?.getBoolean("isAvailable") ?: false
                binding.switchAvailability.setOnCheckedChangeListener(null)
                binding.switchAvailability.isChecked = isAvailable
                binding.switchAvailability.setOnCheckedChangeListener { _, checked ->
                    updateAvailability(checked)
                }
                binding.tvAvailabilityStatus.text =
                    if (isAvailable) "🟢 You are Online" else "⚫ You are Offline"
            }
    }

    private fun updateAvailability(isAvailable: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        db.collection(Constants.COLLECTION_WORKERS).document(uid)
            .set(mapOf("isAvailable" to isAvailable), SetOptions.merge())
            .addOnSuccessListener {
                val msg = if (isAvailable) "You are now visible to customers!"
                else "You are now hidden from search"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                binding.switchAvailability.setOnCheckedChangeListener(null)
                binding.switchAvailability.isChecked = !isAvailable
                binding.switchAvailability.setOnCheckedChangeListener { _, c -> updateAvailability(c) }
                Toast.makeText(this, "Failed to update availability", Toast.LENGTH_SHORT).show()
            }
    }

    // ── Save profile ───────────────────────────────────────────────
    private fun saveProfile() {
        val name  = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val rate  = binding.etDailyRate.text.toString().trim()
        val loc   = binding.etLocation.text.toString().trim()
        val skill = selectedSkill()

        if (name.isEmpty() || phone.isEmpty() || rate.isEmpty()
            || loc.isEmpty() || skill.isEmpty()) {
            Toast.makeText(this, "Please fill all fields and select a skill",
                Toast.LENGTH_SHORT).show()
            return
        }

        setSaving(true)

        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED -> fetchLocationThenSave()
            else -> locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun fetchLocationThenSave() {
        LocationHelper.getCurrentLocation(this) { location ->
            val lat = location?.latitude  ?: 0.0
            val lng = location?.longitude ?: 0.0
            proceedWithSave(lat, lng)
        }
    }

    private fun proceedWithSave(lat: Double, lng: Double) {
        val name  = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val rate  = binding.etDailyRate.text.toString().trim().toIntOrNull() ?: 0
        val loc   = binding.etLocation.text.toString().trim()
        val skill = selectedSkill()
        val uid   = auth.currentUser?.uid ?: run { setSaving(false); return }

        if (profilePhotoUri != null) {
            uploadProfilePhoto(uid, name, phone, rate, loc, skill, lat, lng)
        } else {
            uploadWorkPhotos(uid, name, phone, rate, loc, skill, "", lat, lng)
        }
    }

    // ── Upload profile photo ───────────────────────────────────────
    private fun uploadProfilePhoto(
        uid: String, name: String, phone: String, rate: Int,
        loc: String, skill: String, lat: Double, lng: Double
    ) {
        val ref = storage.reference.child("${Constants.STORAGE_PROFILES}/$uid.jpg")
        ref.putFile(profilePhotoUri!!)
            .addOnProgressListener { snap ->
                val pct = (100.0 * snap.bytesTransferred / snap.totalByteCount).toInt()
                binding.btnSaveProfile.text = "Uploading photo $pct%..."
            }
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception!!
                ref.downloadUrl
            }
            .addOnSuccessListener { url ->
                uploadWorkPhotos(uid, name, phone, rate, loc, skill, url.toString(), lat, lng)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this,
                    "Profile photo upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                setSaving(false)
            }
    }

    // ── Upload work photos ─────────────────────────────────────────
    private fun uploadWorkPhotos(
        uid: String, name: String, phone: String, rate: Int,
        loc: String, skill: String, profileUrl: String, lat: Double, lng: Double
    ) {
        if (workPhotoUris.isEmpty()) {
            saveToFirestore(uid, name, phone, rate, loc, skill, profileUrl, emptyList(), lat, lng)
            return
        }

        val uploadedUrls = mutableListOf<String>()
        var completed = 0
        val total = workPhotoUris.size

        workPhotoUris.forEachIndexed { index, uri ->
            val ref = storage.reference.child("${Constants.STORAGE_WORK}/$uid/$index.jpg")
            ref.putFile(uri)
                .addOnProgressListener { snap ->
                    val pct = (100.0 * snap.bytesTransferred / snap.totalByteCount).toInt()
                    binding.btnSaveProfile.text = "Work photo ${index + 1}/$total $pct%..."
                }
                .continueWithTask { task ->
                    if (!task.isSuccessful) throw task.exception!!
                    ref.downloadUrl
                }
                .addOnSuccessListener { url ->
                    uploadedUrls.add(url.toString())
                    completed++
                    if (completed == total)
                        saveToFirestore(uid, name, phone, rate, loc, skill,
                            profileUrl, uploadedUrls, lat, lng)
                }
                .addOnFailureListener { e ->
                    completed++
                    Toast.makeText(this,
                        "Work photo ${index + 1} failed: ${e.message}",
                        Toast.LENGTH_SHORT).show()
                    if (completed == total)
                        saveToFirestore(uid, name, phone, rate, loc, skill,
                            profileUrl, uploadedUrls, lat, lng)
                }
        }
    }

    // ── Save to Firestore ──────────────────────────────────────────
    private fun saveToFirestore(
        uid: String, name: String, phone: String, rate: Int,
        loc: String, skill: String, profileUrl: String,
        workUrls: List<String>, lat: Double, lng: Double
    ) {
        val worker = Worker(uid, name, phone, skill, rate, loc,
            lat, lng, profileUrl, workUrls, false)

        db.collection(Constants.COLLECTION_WORKERS).document(uid)
            .set(worker)
            .addOnSuccessListener {
                Toast.makeText(this, "✅ Profile saved!", Toast.LENGTH_SHORT).show()
                setSaving(false)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this,
                    "Firestore save failed: ${e.message}", Toast.LENGTH_LONG).show()
                setSaving(false)
            }
    }

    // ── Gemini AI ──────────────────────────────────────────────────
    private fun generateBioWithAI() {
        val name  = binding.etName.text.toString().trim()
        val rate  = binding.etDailyRate.text.toString().trim()
        val loc   = binding.etLocation.text.toString().trim()
        val skill = selectedSkill()

        if (name.isEmpty() || skill.isEmpty()) {
            Toast.makeText(this, "Fill name and select skill first", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnGenerateBio.isEnabled = false
        binding.btnGenerateBio.text = "..."
        binding.tvGeneratedBio.text = "✨ Generating your bio..."

        lifecycleScope.launch {
            val bio = GeminiHelper.generateWorkerBio(
                name, skill, rate.toIntOrNull() ?: 0, loc)
            binding.tvGeneratedBio.text = bio
            binding.btnGenerateBio.isEnabled = true
            binding.btnGenerateBio.text = "Generate"
        }
    }

    private fun suggestSkillWithAI() {
        val description = binding.etWorkDescription.text.toString().trim()
        if (description.isEmpty()) {
            Toast.makeText(this, "Please describe your work first", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSuggestSkill.isEnabled = false
        binding.btnSuggestSkill.text = "..."
        binding.tvSuggestedSkill.text = "🤖 Thinking..."

        lifecycleScope.launch {
            val skill = GeminiHelper.suggestSkillsFromDescription(description)
            binding.tvSuggestedSkill.text = "✅ Suggested: $skill"
            binding.btnSuggestSkill.isEnabled = true
            binding.btnSuggestSkill.text = "Suggest"

            for (i in 0 until binding.chipGroupSkills.childCount) {
                val chip = binding.chipGroupSkills.getChildAt(i) as Chip
                if (chip.text.toString().equals(skill, ignoreCase = true)) {
                    chip.isChecked = true
                    Toast.makeText(this@WorkerProfileActivity,
                        "Skill set to $skill!", Toast.LENGTH_SHORT).show()
                    break
                }
            }
        }
    }

    // ── Helpers ────────────────────────────────────────────────────
    private fun selectedSkill(): String =
        (0 until binding.chipGroupSkills.childCount)
            .map { binding.chipGroupSkills.getChildAt(it) as Chip }
            .firstOrNull { it.isChecked }?.text?.toString() ?: ""

    private fun setSaving(saving: Boolean) {
        binding.btnSaveProfile.isEnabled = !saving
        binding.btnSaveProfile.text = if (saving) "Saving..." else "Save Profile"
    }

    private fun resetSaveButton() = setSaving(false)
}