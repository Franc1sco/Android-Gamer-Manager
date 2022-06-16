package com.example.audiovisualmanager.activity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        if(intent.hasExtra("USERID")){
            userId=intent.getIntExtra("USERID", 0)
        }

        binding.buttonUpdatePassword.setOnClickListener {
            if (binding.editPasswordUpdate.text.toString().isNotEmpty() && binding.editPasswordUpdate.text.toString() == binding.editPasswordUpdateRepeat.text.toString()) {
                dbHandler.updatePassword(userId, binding.editPasswordUpdate.text.toString())
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
    }

    @Override
    override fun onBackPressed() {
        val intent2= Intent (this ,MainListActivity::class.java)
        intent2.putExtra("USERID", userId)
        startActivity(intent2)
        finish()
    }
}