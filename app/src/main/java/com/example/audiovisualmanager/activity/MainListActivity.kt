package com.example.audiovisualmanager.activity

import android.R
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.audiovisualmanager.databinding.ActivityMainlistBinding
import com.example.audiovisualmanager.model.Game
import com.example.audiovisualmanager.utils.Constants
import com.example.audiovisualmanager.utils.Constants.FINALIZADO
import com.example.audiovisualmanager.utils.Constants.PENDIENTE
import com.example.audiovisualmanager.adapter.GameAdapter
import com.example.audiovisualmanager.database.MysqlManager
import java.util.*


class MainListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainlistBinding
    private lateinit var listDataAdapter: ArrayList<Game>
    private lateinit var listDataFullAdapter: ArrayList<Game>
    private var dbHandler: MysqlManager = MysqlManager().getInstance()
    private lateinit var adapter: GameAdapter
    private var userId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("USERID")){
            userId=intent.getIntExtra("USERID", 0)
        }

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
        //getGames()?.let { listDataAdapter.addAll(it.sortedBy { element -> element.name }) }
        val gameList = dbHandler.getGamesPendingByUserid(userId)
        listDataAdapter.addAll(gameList)
        listDataFullAdapter.addAll(listDataAdapter)
        adapter = GameAdapter(listDataAdapter)
        binding.recyclerView.adapter = adapter

        binding.statusList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                when (position) {
                    Constants.ITEM_TODOS -> {
                        filterOnly(null)
                    }
                    Constants.ITEM_PENDIENTE -> {
                        filterOnly(PENDIENTE)
                    }
                    Constants.ITEM_FINALIZADO -> {
                        filterOnly(FINALIZADO)
                    }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        binding.AddGame.setOnClickListener{

            val intent2= Intent (this ,AddGameActivity::class.java)
            intent2.putExtra("USERID", userId)
            startActivity(intent2)
            finish()

        }
    }

    private fun filterOnly(filter: String?) {
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

    /*
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
    }*/
}