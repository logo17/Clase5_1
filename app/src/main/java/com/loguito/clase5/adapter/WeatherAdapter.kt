package com.loguito.clase5.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.loguito.clase5.databinding.WeatherCellBinding
import com.loguito.clase5.models.Weather
import com.loguito.clase5.models.WeatherDetail
import com.squareup.picasso.Picasso
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    private val clicksAcceptor = PublishSubject.create<Weather>()

    val onItemClicked: Observable<Weather> = clicksAcceptor.hide()

    var weathers: List<WeatherDetail> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class WeatherViewHolder(private val binding: WeatherCellBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(weather: Weather) {
            binding.titleTextView.text = weather.main
            binding.descriptionTextView.text = weather.description
            Picasso.get().load("https://openweathermap.org/img/wn/${weather.icon}.png").into(binding.iconImageView)

            binding.root.setOnClickListener {
                clicksAcceptor.onNext(weather)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = WeatherCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(weathers[position].weather.first())
    }

    override fun getItemCount(): Int = weathers.size
}