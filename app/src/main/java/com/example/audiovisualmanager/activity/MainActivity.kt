package com.example.audiovisualmanager.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.audiovisualmanager.database.MysqlManager
import com.example.audiovisualmanager.databinding.ActivityMainBinding
import com.example.audiovisualmanager.utils.Constants

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var dbHandler: MysqlManager = MysqlManager().getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHandler.getConnection()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getSavedSession()

        binding.buttonLogin.setOnClickListener {
            val isValidUser = dbHandler.isValidUser(binding.editTextUser.text.toString(), binding.editTextPassword.text.toString())
            if (isValidUser == null) {
                Toast.makeText(this, "Error on mysql connection, restart app and try again", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (isValidUser) {
                val userId = dbHandler.getUserId(binding.editTextUser.text.toString())
                if (userId == null) {
                    Toast.makeText(this, "Error on mysql connection, restart app and try again", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                checkSaveSession()
                val intent = Intent(this, MainListActivity::class.java)
                intent.putExtra("USERID", userId)
                startActivity(intent)
            } else {
                Toast.makeText(this, "invalid user or password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.cbShowPassword.setOnClickListener {
            if (binding.cbShowPassword.isChecked) {
                binding.editTextPassword.inputType = TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.editTextPassword.inputType = Constants.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
    }

    private fun checkSaveSession() {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        if (binding.checkBoxSaveSession.isChecked) {
            sharedPref.edit().putString("USER", binding.editTextUser.text.toString()).apply()
            sharedPref.edit().putString("PASSWORD", binding.editTextPassword.text.toString()).apply()
        } else {
            sharedPref.edit().remove("USER").apply()
            sharedPref.edit().remove("PASSWORD").apply()
        }
    }

    private fun getSavedSession() {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val user = sharedPref.getString("USER", null)
        val password = sharedPref.getString("PASSWORD", null)
        if (user != null && password != null) {
            binding.editTextUser.setText(user)
            binding.editTextPassword.setText(password)
            binding.checkBoxSaveSession.isChecked = true
        }
    }
}