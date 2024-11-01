package com.example.skycast.view.favourite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.databinding.DayDetailsBinding
import com.example.skycast.databinding.ItemFavouriteBinding
import com.example.skycast.model.pojo.weatherEntity.WeatherEntity
import com.example.skycast.view.homeview.DayDetailsAdapter
import com.example.skycast.view.homeview.DayDetailsAdapter.ViewHolder

class FavouriteAdapter(var favList :List<WeatherEntity> ,  private var onCardClick: (WeatherEntity)-> Unit ): RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {

    lateinit var  binding : ItemFavouriteBinding

    class ViewHolder(val binding: ItemFavouriteBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateList(newList : List<WeatherEntity>){
        this.favList = newList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemFavouriteBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return ViewHolder(binding)

    }



    override fun onBindViewHolder(holder: FavouriteAdapter.ViewHolder, position: Int) {
        var currentObj= favList.get(position)
        binding.tvCityName.text = currentObj.cityName
        binding.tvTempunitValue.text= currentObj.main.temp.toString()
        binding.cardTempUnit.setOnClickListener {
            onCardClick.invoke(currentObj)
        }
    }
    override fun getItemCount()= favList.size

}