package com.example.audiovisualmanager.activity

import android.R
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.audiovisualmanager.adapter.UserAdapter
import com.example.audiovisualmanager.database.MysqlManager
import com.example.audiovisualmanager.databinding.UserlistActivityBinding
import com.example.audiovisualmanager.model.User
import com.example.audiovisualmanager.utils.Constants
import java.util.ArrayList

class UserListActivity : AppCompatActivity() {
    private lateinit var binding: UserlistActivityBinding
    private lateinit var listDataAdapter: ArrayList<User>
    private lateinit var listDataFullAdapter: ArrayList<User>
    private var dbHandler: MysqlManager = MysqlManager().getInstance()
    private lateinit var adapter: UserAdapter
    var userId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserlistActivityBinding .inflate(layoutInflater)
        setContentView(binding.root)
        if(intent.hasExtra("USERID")){
            userId=intent.getIntExtra("USERID", 0)
        }
        setupAdapter()
        setupUpStatusSpinner()
    }

    private fun setupUpStatusSpinner() {
        binding.statusList.adapter =
            ArrayAdapter(this, R.layout.simple_spinner_item, arrayOf("Todos", "Público", "Privado"))

        binding.statusList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        filterOnly(0)
                    }
                    Constants.ITEM_PUBLIC -> {
                        filterOnly(Constants.ITEM_PUBLIC)
                    }
                    Constants.ITEM_PRIVATE -> {
                        filterOnly(Constants.ITEM_PRIVATE)
                    }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }
    }

    private fun filterOnly(filter: Int) {
        listDataAdapter.clear()
        if (filter != 0) {
            when (filter) {
                Constants.ITEM_PUBLIC -> {
                    listDataAdapter.addAll(listDataFullAdapter.filter { it.private == 0 })
                }
                Constants.ITEM_PRIVATE -> {
                    listDataAdapter.addAll(listDataFullAdapter.filter { it.private == 1 })
                }
            }
        } else {
            listDataAdapter.addAll(listDataFullAdapter)
        }
        adapter.notifyDataSetChanged()
    }

    private fun setupAdapter() {
        binding.recyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        binding.recyclerView.setHasFixedSize(true)
        listDataAdapter = ArrayList<User>()
        listDataFullAdapter = ArrayList<User>()
        listDataFullAdapter = dbHandler.getUserList(userId)
        listDataAdapter.addAll(listDataFullAdapter)
        adapter = UserAdapter(listDataAdapter)
        binding.recyclerView.adapter = adapter
    }
}