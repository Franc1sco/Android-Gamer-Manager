package com.example.audiovisualmanager.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.audiovisualmanager.database.DatabaseManager
import com.example.audiovisualmanager.database.MysqlManager
import com.example.audiovisualmanager.databinding.RegisterActivityBinding
import com.example.audiovisualmanager.model.User

class RegisterActivity : AppCompatActivity() {
    private var dbHandler: MysqlManager = MysqlManager().getInstance()
    private lateinit var binding: RegisterActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonDoRegister.setOnClickListener {
            val name = binding.editTextUser.text.toString()
            val pass = binding.editTextPassword.text.toString()
            if (name.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                val user = User(name, pass)
                if (dbHandler.checkUserExists(user.name)) {
                    Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
                } else {
                    dbHandler.addUser(user)
                    Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

}
