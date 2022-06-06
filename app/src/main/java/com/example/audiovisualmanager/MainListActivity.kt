package com.example.audiovisualmanager

import android.R
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.audiovisualmanager.databinding.ActivityMainlistBinding


class MainListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainlistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadStatusSpinner()
        loadOrderSpinner()
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
        val array = listOf("Status", "Pendiente", "Finalizado")
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