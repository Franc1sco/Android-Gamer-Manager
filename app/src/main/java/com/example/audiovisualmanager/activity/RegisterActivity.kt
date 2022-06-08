package com.example.audiovisualmanager.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.audiovisualmanager.database.DatabaseManager
import com.example.audiovisualmanager.databinding.RegisterActivityBinding
import com.example.audiovisualmanager.model.User

class RegisterActivity : AppCompatActivity() {
    private lateinit var dbHandler: DatabaseManager
    private lateinit var binding: RegisterActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHandler = DatabaseManager(this)

        binding.buttonDoRegister.setOnClickListener {
            val name = binding.editTextUser.text.toString()
            val pass = binding.editTextPassword.text.toString()
            if (name.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                val user = User(name, pass)
                dbHandler.addUser(user)
                Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

}
