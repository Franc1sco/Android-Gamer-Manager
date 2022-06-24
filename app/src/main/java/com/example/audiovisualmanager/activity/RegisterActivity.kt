package com.example.audiovisualmanager.activity

import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.audiovisualmanager.R
import com.example.audiovisualmanager.database.MysqlManager
import com.example.audiovisualmanager.databinding.RegisterActivityBinding
import com.example.audiovisualmanager.model.User
import com.example.audiovisualmanager.utils.Constants
import com.example.audiovisualmanager.utils.Utils

class RegisterActivity : AppCompatActivity() {
    private var dbHandler: MysqlManager = MysqlManager().getInstance()
    private lateinit var binding: RegisterActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonDoRegister.setOnClickListener {
            val name = binding.editTextUserRegister.text.toString()
            val pass = binding.editTextPasswordRegister.text.toString()
            if (name.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            } else {
                val user = User(name, pass)
                val userExits = dbHandler.checkUserExists(user.name)
                if (userExits == null) {
                    Utils.connectionError(this)
                    return@setOnClickListener
                }
                if (userExits) {
                    Toast.makeText(this, getString(R.string.user_already_extis), Toast.LENGTH_SHORT).show()
                } else {
                    if (dbHandler.addUser(user)) {
                        Utils.connectionError(this)
                        return@setOnClickListener
                    }
                    Toast.makeText(this, getString(R.string.user_registered_success), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        binding.cbShowPasswordRegister.setOnClickListener {
            if (binding.cbShowPasswordRegister.isChecked) {
                binding.editTextPasswordRegister.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.editTextPasswordRegister.inputType = Constants.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
    }
}
