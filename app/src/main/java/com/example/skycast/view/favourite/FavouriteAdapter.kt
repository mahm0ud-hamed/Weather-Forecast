package com.example.skycast.view.favourite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.databinding.ItemFavouriteBinding
import com.example.skycast.model.pojo.weatherEntity.WeatherEntity

class FavouriteAdapter(
    private var favList: List<WeatherEntity>,
    private val onDeleteClick: (WeatherEntity) -> Unit,
    private val onCardClick: (WeatherEntity) -> Unit
) : RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemFavouriteBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateList(newList: List<WeatherEntity>) {
        favList = newList
        notifyDataSetChanged() // Notify adapter that the data has changed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFavouriteBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentObj = favList[position]
        holder.binding.tvCityName.text = currentObj.cityName
        holder.binding.tvTempunitValue.text = "${currentObj.main.tempMin}/${currentObj.main.tempMax}"

        holder.binding.cardTempUnit.setOnClickListener {
            onCardClick.invoke(currentObj)
        }

        holder.binding.btnDeletFav.setOnClickListener {
            onDeleteClick.invoke(currentObj)
        }
    }

    override fun getItemCount() = favList.size
}
