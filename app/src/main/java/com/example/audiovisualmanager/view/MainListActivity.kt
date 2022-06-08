package com.example.audiovisualmanager.view

import android.R
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.audiovisualmanager.data.APIService
import com.example.audiovisualmanager.data.DatabaseHelper
import com.example.audiovisualmanager.data.GameModel
import com.example.audiovisualmanager.data.RemoteDataMapper
import com.example.audiovisualmanager.databinding.ActivityMainlistBinding
import com.example.audiovisualmanager.model.Game
import com.example.audiovisualmanager.utils.Constants
import com.example.audiovisualmanager.utils.Constants.FINALIZADO
import com.example.audiovisualmanager.utils.Constants.PENDIENTE
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class MainListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainlistBinding
    private lateinit var listDataAdapter: ArrayList<Game>
    private lateinit var listDataFullAdapter: ArrayList<Game>
    private lateinit var adapter: GameAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadStatusSpinner()
        loadOrderSpinner()
        setupAdapter()
    }

    private fun setupAdapter() {
        binding.recyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        binding.recyclerView.setHasFixedSize(true)
        listDataAdapter = ArrayList<Game>()
        listDataFullAdapter = ArrayList<Game>()
        getGames()?.let { listDataAdapter.addAll(it.sortedBy { element -> element.name }) }
        listDataAdapter.addAll(DatabaseHelper().generateGameList().sortedBy { it.name })
        listDataFullAdapter.addAll(listDataAdapter)
        adapter = GameAdapter(listDataAdapter)
        binding.recyclerView.adapter = adapter

        binding.statusList.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                when (position) {
                    Constants.ITEM_TODOS -> {
                        FilterOnly(null)
                    }
                    Constants.ITEM_PENDIENTE -> {
                        FilterOnly(PENDIENTE)
                    }
                    Constants.ITEM_FINALIZADO -> {
                        FilterOnly(FINALIZADO)
                    }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        })
    }

    private fun FilterOnly(filter: String?) {
        listDataAdapter.clear()
        if (filter != null) {
            for (i in listDataFullAdapter.indices) {
                if (listDataFullAdapter[i].status == filter) {
                    listDataAdapter.add(listDataFullAdapter[i])
                }
            }
        } else {
            listDataAdapter.addAll(listDataFullAdapter)
        }
        adapter.notifyDataSetChanged()
    }

    private fun loadOrderSpinner() {
        val array = listOf("Orden", "Plataforma", "Studio", "Género", "Valoración")
        binding.plaId.adapter = object : ArrayAdapter<String>(this, R.layout.simple_list_item_1, array) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val element = view as TextView
                if (position == 0) {
                    element.setTextColor(Color.GRAY)
                } else {
                    element.setTextColor(Color.BLACK)
                }
                return view
            }
        }
    }

    private fun loadStatusSpinner() {
        val array = listOf("Status", "Todos", "Pendiente", "Finalizado")
        binding.statusList.adapter = object : ArrayAdapter<String>(this, R.layout.simple_list_item_1, array) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val element = view as TextView
                if (position == 0) {
                    element.setTextColor(Color.GRAY)
                } else {
                    element.setTextColor(Color.BLACK)
                }
                return view
            }
        }
    }

    fun getGames(): List<Game>? {
        var results: List<Game>? = null
        val apiService = getRetrofit().create(APIService::class.java)
        try {
            val call = apiService.getGames()
            val heroes = call.body()
            if(call.body()?.count != 0) {
                results = RemoteDataMapper.mapListCharacterRestModelToCharacter(heroes?.results?.toList())
            }else{

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }



        return results
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.rawg.io")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}