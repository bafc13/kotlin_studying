package com.example.lab1_bafc13.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lab1_bafc13.databinding.ItemFavoriteBinding
import com.example.lab1_bafc13.models.FavoriteRadar

class FavoriteRadarAdapter(
    private val radars: MutableList<FavoriteRadar> = mutableListOf(),
    private val onRadarClick: (FavoriteRadar) -> Unit = {},
    private val onDeleteButtonClick: (FavoriteRadar) -> Unit = {}
) : RecyclerView.Adapter<FavoriteRadarAdapter.FavoriteRadarViewHolder>() {

    inner class FavoriteRadarViewHolder(
        private val binding: ItemFavoriteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(radar: FavoriteRadar) {
            binding.xCoord.text = radar.gps_x.toString()
            binding.yCoord.text = radar.gps_y.toString()

            binding.root.setOnClickListener {
                onRadarClick(radar)
            }
            binding.deleteButton.setOnClickListener {
                onDeleteButtonClick(radar)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteRadarViewHolder {
        val binding = ItemFavoriteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteRadarViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FavoriteRadarViewHolder,
        position: Int
    ) {
        holder.bind(radars[position])
    }

    override fun getItemCount(): Int = radars.size

    fun updateRadars(newRadars: List<FavoriteRadar>) {
        radars.clear()
        radars.addAll(newRadars)
        notifyDataSetChanged()
    }
}