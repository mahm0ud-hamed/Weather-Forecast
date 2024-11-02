package com.example.skycast.view.alarm

import android.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.databinding.ItemAlarmBinding
import com.example.skycast.databinding.ItemFavouriteBinding
import com.example.skycast.model.pojo.alarmentity.WeatherAlarm
import com.example.skycast.Util.formatMillisToDateTime
import com.example.skycast.model.pojo.weatherEntity.WeatherEntity
import com.google.android.material.snackbar.Snackbar


class AlarmAdapter(var alarmList : List<WeatherAlarm>, private var onCardClick: (WeatherAlarm)-> Unit):  RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {
    lateinit var  binding : ItemAlarmBinding


    fun updateList(newList : List<WeatherAlarm>){
        this.alarmList = newList
    }
    class ViewHolder(val binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentObj= alarmList.get(position)
        binding.tvALarmInfo.text = formatMillisToDateTime(currentObj.triggerAtMillis )
        binding.btnDeleteAlarm.setOnClickListener {
            onCardClick.invoke(currentObj)
        }
    }




    override fun getItemCount()= alarmList.size


}