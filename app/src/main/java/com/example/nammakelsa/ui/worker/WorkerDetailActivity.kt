package com.example.nammakelsa.ui.worker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.nammakelsa.databinding.ActivityWorkerDetailBinding

class WorkerDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkerDetailBinding

    companion object {
        const val EXTRA_NAME     = "WORKER_NAME"
        const val EXTRA_SKILL    = "WORKER_SKILL"
        const val EXTRA_RATE     = "WORKER_RATE"
        const val EXTRA_LOCATION = "WORKER_LOCATION"
        const val EXTRA_PHONE    = "WORKER_PHONE"
        const val EXTRA_PHOTO    = "WORKER_PHOTO"
        const val EXTRA_GALLERY  = "WORKER_GALLERY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name     = intent.getStringExtra(EXTRA_NAME) ?: ""
        val skill    = intent.getStringExtra(EXTRA_SKILL) ?: ""
        val rate     = intent.getIntExtra(EXTRA_RATE, 0)
        val location = intent.getStringExtra(EXTRA_LOCATION) ?: ""
        val phone    = intent.getStringExtra(EXTRA_PHONE) ?: ""
        val photoUrl = intent.getStringExtra(EXTRA_PHOTO) ?: ""
        val gallery  = intent.getStringArrayListExtra(EXTRA_GALLERY) ?: arrayListOf()

        // Fill UI
        binding.tvDetailName.text     = name
        binding.tvDetailSkill.text    = skill
        binding.tvDetailRate.text     = "₹$rate"
        binding.tvDetailLocation.text = location

        // Description below name
        val description = "$name is a skilled $skill " +
                "based in $location. Available at ₹$rate " +
                "for a full day's work. Tap Call to hire directly."
        binding.tvDetailDescription.text = description

        // Profile photo
        if (photoUrl.isNotEmpty()) {
            Glide.with(this)
                .load(photoUrl)
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .error(android.R.drawable.stat_notify_error)
                .centerCrop()
                .into(binding.ivDetailPhoto)
        }

        // Work gallery
        if (gallery.isNotEmpty()) {
            binding.cardGallery.visibility = View.VISIBLE
            val imageViews = listOf(binding.ivGallery1, binding.ivGallery2, binding.ivGallery3)
            gallery.forEachIndexed { i, url ->
                if (i < imageViews.size && url.isNotEmpty())
                    Glide.with(this)
                        .load(url)
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                        .centerCrop()
                        .into(imageViews[i])
            }
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnDetailCall.setOnClickListener {
            if (phone.isEmpty()) {
                Toast.makeText(this, "No phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
        }
    }
}