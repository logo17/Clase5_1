package com.loguito.clase5.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import com.loguito.clase5.R
import com.loguito.clase5.adapter.WeatherAdapter
import com.loguito.clase5.databinding.FragmentWeatherListBinding
import com.loguito.clase5.viewmodels.WeatherListViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.BiFunction
import kotlinx.android.synthetic.main.fragment_weather_list.*
import java.util.concurrent.TimeUnit

class WeatherListFragment : Fragment(R.layout.fragment_weather_list) {
    private var _binding: FragmentWeatherListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WeatherListViewModel by viewModels()

    private val disposable = CompositeDisposable()

    private val adapter = WeatherAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.weatherRecyclerView.adapter = adapter

        // TODO Nos suscribimos al evento, que es disparado por el viewmodel cuando el request se completa
        viewModel.getWeatherList().observe(viewLifecycleOwner) {
            adapter.weathers = it
        }

        // Listener para cuando el texto cambia - VERSION CLASICA
//        binding.searchBox.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                Log.d("TEST", p0.toString())
//            }
//
//        })

        //FORMA RX - REACTIVA
        disposable.add(
            binding.searchBox.textChanges()
                .skipInitialValue()
                .debounce(300, TimeUnit.MILLISECONDS)
                .map { it.toString() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    binding.textInputLayout.error = if (it.isEmpty()) "Campo requerido" else null
                }
        )

        disposable.add(
            Observable.combineLatest(binding.searchBox.textChanges(), binding.quantityBox.textChanges(),
                { queryText, quantity -> queryText.toString().isNotEmpty() && quantity.toString().isNotEmpty() })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    binding.searchButton.isEnabled = it
                }
        )

        disposable.add(
            binding.searchButton.clicks()
                .subscribe { viewModel.makeAPIRequest(binding.searchBox.text.toString(), binding.quantityBox.text.toString().toInt()) }
        )

        disposable.add(
            adapter.onItemClicked
                .throttleFirst(400, TimeUnit.MILLISECONDS)
                .subscribe {
                    Log.d("TEST", "Item clicked: ${it.main}")
                }
        )
    }
}