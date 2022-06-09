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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.audiovisualmanager.database.MysqlManager
import com.example.audiovisualmanager.databinding.AddgameActivityBinding
import com.example.audiovisualmanager.model.Game


class AddGameActivity: AppCompatActivity() {
    private lateinit var binding: AddgameActivityBinding
    private var userId: Int = 0
    private var dbHandler: MysqlManager = MysqlManager().getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddgameActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("USERID")){
            userId=intent.getIntExtra("USERID", 0)
        }

        loadStatusSpinner()
        loadPlatformSpinner()

        binding.buttonDoRegister.setOnClickListener{
            if (binding.editTextGameName.text.isEmpty()) {
                Toast.makeText(this, "Please enter a game name", Toast.LENGTH_SHORT).show()
            } else if (binding.spinnerStatus.selectedItemPosition <= 0) {
                Toast.makeText(this, "Please select a status", Toast.LENGTH_SHORT).show()
            } else if (binding.spinnerPlatform.selectedItemPosition <= 0) {
                Toast.makeText(this, "Please select a platform", Toast.LENGTH_SHORT).show()
            } else {
                dbHandler.addGameByUserid(Game(
                    binding.editTextGameName.text.toString(),
                    binding.spinnerStatus.selectedItem.toString(),
                    binding.spinnerPlatform.selectedItem.toString()),
                    userId
                )
                val intent2= Intent (this ,MainListActivity::class.java)
                intent2.putExtra("USERID", userId)
                startActivity(intent2)
                finish()

            }

        }
    }

    private fun loadStatusSpinner() {
        val array = listOf("Status", "Todos", "Pendiente", "Finalizado")
        binding.spinnerStatus.adapter = object : ArrayAdapter<String>(this, R.layout.simple_list_item_1, array) {
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

    private fun loadPlatformSpinner() {
        val array = listOf("Plataforma", "PC", "XBOX", "PLAYSTATION")
        binding.spinnerPlatform.adapter = object : ArrayAdapter<String>(this, R.layout.simple_list_item_1, array) {
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

    @Override
    override fun onBackPressed() {
        val intent2= Intent (this ,MainListActivity::class.java)
        intent2.putExtra("USERID", userId)
        startActivity(intent2)
        finish()
    }
}
