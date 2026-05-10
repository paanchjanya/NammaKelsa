package com.example.nammakelsa.ui.customer

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.nammakelsa.R
import com.example.nammakelsa.databinding.ItemWorkerCardBinding
import com.example.nammakelsa.model.Worker
import com.example.nammakelsa.ui.worker.WorkerDetailActivity
import com.example.nammakelsa.utils.LocationHelper

class WorkerAdapter(
    private var workers: List<Worker> = emptyList(),
    private val onCallClick: (Worker) -> Unit
) : RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder>() {

    private var distances: Map<String, Double?> = emptyMap()

    inner class WorkerViewHolder(val binding: ItemWorkerCardBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkerViewHolder {
        val binding = ItemWorkerCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WorkerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkerViewHolder, position: Int) {
        val worker = workers[position]
        val dist = distances[worker.uid]

        with(holder.binding) {
            tvWorkerName.text = worker.name
            tvSkillBadge.text = worker.skillType
            tvRate.text       = root.context.getString(R.string.worker_rate_format, worker.dailyRate)

            // Show distance if available, else show location name
            tvLocation.text = if (dist != null)
                root.context.getString(R.string.location_format, LocationHelper.formatDistance(dist))
            else
                root.context.getString(R.string.location_format, worker.locationName)

            // Handle worker photo
            if (worker.profilePhotoUrl.isNotEmpty()) {
                Glide.with(ivWorkerPhoto.context)
                    .load(worker.profilePhotoUrl)
                    .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                    .error(android.R.drawable.stat_notify_error)
                    .circleCrop()
                    .into(ivWorkerPhoto)
            } else {
                ivWorkerPhoto.setImageResource(android.R.drawable.ic_menu_gallery)
                ivWorkerPhoto.setBackgroundColor(
                    android.graphics.Color.parseColor("#FFF0E8")
                )
            }

            // Call button
            btnCall.setOnClickListener { onCallClick(worker) }

            // Tap card → open detail screen
            root.setOnClickListener {
                val context = root.context
                val intent = Intent(context, WorkerDetailActivity::class.java).apply {
                    putExtra(WorkerDetailActivity.EXTRA_NAME,     worker.name)
                    putExtra(WorkerDetailActivity.EXTRA_SKILL,    worker.skillType)
                    putExtra(WorkerDetailActivity.EXTRA_RATE,     worker.dailyRate)
                    putExtra(WorkerDetailActivity.EXTRA_LOCATION, worker.locationName)
                    putExtra(WorkerDetailActivity.EXTRA_PHONE,    worker.phone)
                    putExtra(WorkerDetailActivity.EXTRA_PHOTO,    worker.profilePhotoUrl)
                    putStringArrayListExtra(
                        WorkerDetailActivity.EXTRA_GALLERY,
                        ArrayList(worker.workPhotoUrls)
                    )
                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = workers.size

    fun updateList(newList: List<Worker>, newDistances: Map<String, Double?> = emptyMap()) {
        val diffCallback = WorkerDiffCallback(workers, newList, distances, newDistances)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        workers = newList
        distances = newDistances
        diffResult.dispatchUpdatesTo(this)
    }

    class WorkerDiffCallback(
        private val oldList: List<Worker>,
        private val newList: List<Worker>,
        private val oldDistances: Map<String, Double?>,
        private val newDistances: Map<String, Double?>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].uid == newList[newItemPosition].uid
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            val oldDist = oldDistances[oldItem.uid]
            val newDist = newDistances[newItem.uid]

            return oldItem == newItem && oldDist == newDist
        }
    }
}