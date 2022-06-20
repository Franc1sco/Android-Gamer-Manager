package com.example.audiovisualmanager.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.audiovisualmanager.R
import com.example.audiovisualmanager.database.MysqlManager
import com.example.audiovisualmanager.databinding.EdituserActivityBinding
import com.example.audiovisualmanager.utils.Constants

class EditUserActivity: AppCompatActivity() {

    private lateinit var binding: EdituserActivityBinding
    private var userId: Int = 0
    private var dbHandler: MysqlManager = MysqlManager().getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EdituserActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPrivateSpinner()
        if(intent.hasExtra("USERID")){
            userId=intent.getIntExtra("USERID", 0)
        }

        binding.buttonUpdatePassword.setOnClickListener {
            if (binding.editPasswordUpdate.text.toString().isNotEmpty() && binding.editPasswordUpdate.text.toString() == binding.editPasswordUpdateRepeat.text.toString()) {
                dbHandler.updateUser(userId, binding.editPasswordUpdate.text.toString(),
                    binding.spinnerUpdatePrivate.selectedItemPosition == 1)
                Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show()
                val intent2= Intent (this ,MainListActivity::class.java)
                intent2.putExtra("USERID", userId)
                startActivity(intent2)
                finish()
            } else {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cbShowPasswordUpdate.setOnClickListener {
            if (binding.cbShowPasswordUpdate.isChecked) {
                binding.editPasswordUpdate.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.editPasswordUpdateRepeat.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.editPasswordUpdate.inputType = Constants.TYPE_TEXT_VARIATION_PASSWORD
                binding.editPasswordUpdateRepeat.inputType = Constants.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        binding.buttonViewUsers.setOnClickListener {
            val intent2= Intent (this ,UserListActivity::class.java)
            intent2.putExtra("USERID", userId)
            startActivity(intent2)
            //finish()
        }
    }

    private fun setupPrivateSpinner() {
        val array = listOf("Público", "Privado")
        binding.spinnerUpdatePrivate.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, array)
    }

    @Override
    override fun onBackPressed() {
        val intent2= Intent (this ,MainListActivity::class.java)
        intent2.putExtra("USERID", userId)
        startActivity(intent2)
        finish()
    }
}