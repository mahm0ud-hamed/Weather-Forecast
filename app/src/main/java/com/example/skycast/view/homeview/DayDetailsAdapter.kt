package com.example.skycast.view.homeview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skycast.Util.getTimeAsHumanRedable
import com.example.skycast.databinding.DayDetailsBinding
import com.example.skycast.model.pojo.fivedayforecast.List

class DayDetailsAdapter(var states : kotlin.collections.List<List<Any?>> ) : RecyclerView.Adapter<DayDetailsAdapter.ViewHolder>() {
    lateinit var binding: DayDetailsBinding

    var newLable :String = ""
        fun updateList(newStates : kotlin.collections.List<List<Any?>>){
            states = newStates
        }

    class ViewHolder(val binding: DayDetailsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayDetailsAdapter.ViewHolder {
       val inflater = LayoutInflater.from(parent.context)
        binding = DayDetailsBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayDetailsAdapter.ViewHolder, position: Int) {
        var currentObj= states.get(position)
        binding.tvTime.text = getTimeAsHumanRedable(currentObj.dt.toLong())
        binding.tvTemp.text = "${currentObj.main.temp.toInt() } ${newLable}"
        var imgThumbnail = "https://openweathermap.org/img/wn/"
        Glide.with(binding.imgvState.context).load("${imgThumbnail}${currentObj.weather.get(0).icon}@2x.png").into(binding.imgvState)


    }

    override fun getItemCount()= states.size
    fun updateTemperatureLable(newLable : String){
        this.newLable = newLable
    }
}


