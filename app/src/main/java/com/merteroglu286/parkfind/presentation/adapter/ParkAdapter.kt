package com.merteroglu286.parkfind.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.merteroglu286.parkfind.databinding.ItemParkBinding
import com.merteroglu286.parkfind.domain.model.ParkModel
import com.merteroglu286.parkfind.presentation.base.BaseViewHolder
import com.merteroglu286.parkfind.utility.extension.toUri

class ParkAdapter(
    private val onEvent: (Event) -> Unit
) : ListAdapter<ParkModel, ParkAdapter.ParkViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkViewHolder {
        return ParkViewHolder.create(parent, onEvent)
    }

    override fun onBindViewHolder(holder: ParkViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ParkViewHolder(
        private val binding: ItemParkBinding,
        private val onEvent: (Event) -> Unit
    ) : BaseViewHolder<ParkModel>(binding.root) {

        override fun bind(item: ParkModel) {
            with(binding) {
                binding.addressTextView.text = item.address
                dateTextView.text = item.time
                imageView.setImageURI(item.imagePath.toUri())

                goButton.setOnClickListener{
                    onEvent(Event.OnClickGoButton(item.lat,item.lon))
                }

                deleteButton.setOnClickListener{
                    onEvent(Event.OnClickDeleteButton(item))
                }

                binding.root.setOnClickListener {
                    onEvent(Event.OnClickItem(item))
                }

            }
        }

        companion object {
            fun create(parent: ViewGroup, onEvent: (Event) -> Unit): ParkViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemParkBinding.inflate(inflater, parent, false)
                return ParkViewHolder(binding, onEvent)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ParkModel>() {

        override fun areItemsTheSame(oldItem: ParkModel, newItem: ParkModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ParkModel, newItem: ParkModel): Boolean {
            return oldItem == newItem
        }
    }

    sealed interface Event {
        data class OnClickItem(val item: ParkModel) : Event
        data class OnClickGoButton(val lat: Double, val lon: Double) : Event
        data class OnClickDeleteButton(val item : ParkModel) : Event
    }
}