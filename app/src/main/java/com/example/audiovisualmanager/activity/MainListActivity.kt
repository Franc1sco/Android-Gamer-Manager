package com.example.audiovisualmanager.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.audiovisualmanager.R
import com.example.audiovisualmanager.databinding.ActivityMainlistBinding
import com.example.audiovisualmanager.model.Game
import com.example.audiovisualmanager.utils.Constants
import com.example.audiovisualmanager.adapter.GameAdapter
import com.example.audiovisualmanager.database.MysqlManager
import com.example.audiovisualmanager.utils.SwipeToDelete
import com.example.audiovisualmanager.utils.SwipeToEdit
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
        loadMainList()

        val editSwipeHandler = object : SwipeToEdit(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.recyclerView.adapter as GameAdapter
                adapter.notifyEditItem(this@MainListActivity, viewHolder.adapterPosition, userId)
                finish()
            }
        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding.recyclerView)

        val deleteSwipeHandler = object : SwipeToDelete(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.recyclerView.adapter as GameAdapter
                val pos= viewHolder.adapterPosition
                val dialog= AlertDialog.Builder(this@MainListActivity)
                    .setTitle(getString(R.string.alert_dialog_delete))
                    .setMessage(getString(R.string.alert_dialog_confirm_delete))
                    .setNegativeButton(getString(R.string.alert_dialog_no)){ view, _ ->
                        Toast.makeText(this@MainListActivity,getString(R.string.alert_dialog_denied_delete), Toast.LENGTH_LONG).show()
                        view.dismiss()
                    }

                    .setPositiveButton(getString(R.string.alert_dialog_yes)){ view,_ ->
                        adapter.removeAt(pos)
                        Toast.makeText(this@MainListActivity,getString(R.string.alert_dialog_delete_confirmed), Toast.LENGTH_LONG).show()
                        view.dismiss()
                    }
                    .setCancelable(false)
                    .create()
                dialog.show()




            }
        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding.recyclerView)

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
                    Constants.ITEM_PROCESO -> {
                        filterOnly(Constants.EN_PROCESO)
                    }
                    Constants.ITEM_PENDIENTE -> {
                        filterOnly(Constants.PENDIENTE)
                    }
                    Constants.ITEM_FINALIZADO -> {
                        filterOnly(Constants.FINALIZADO)
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

        binding.UserConfig.setOnClickListener{
            val intent2= Intent (this ,EditUserActivity::class.java)
            intent2.putExtra("USERID", userId)
            startActivity(intent2)
            finish()

        }
    }

    private fun loadMainList() {
        listDataAdapter = ArrayList<Game>()
        listDataFullAdapter = ArrayList<Game>()
        listDataFullAdapter = dbHandler.getGamesPendingByUserid(userId)
        listDataAdapter.addAll(listDataFullAdapter)
        adapter = GameAdapter(listDataAdapter)
        binding.recyclerView.adapter = adapter
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
        val array = listOf("Status", "Todos", Constants.PENDIENTE, Constants.EN_PROCESO, Constants.FINALIZADO)
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
}