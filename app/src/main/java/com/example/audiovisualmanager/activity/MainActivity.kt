package com.example.audiovisualmanager.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.audiovisualmanager.database.DatabaseManager
import com.example.audiovisualmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHandler: DatabaseManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHandler = DatabaseManager(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener {
            if (dbHandler.isValidUser(binding.editTextUser.text.toString(), binding.editTextPassword.text.toString())) {
                startActivity(Intent(this, MainListActivity::class.java))
            } else {
                Toast.makeText(this, "invalid user or password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}