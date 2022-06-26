package com.example.audiovisualmanager.view

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.audiovisualmanager.R
import com.example.audiovisualmanager.databinding.EdituserActivityBinding
import com.example.audiovisualmanager.presenter.EditUserPresenter
import com.example.audiovisualmanager.presenter.interfaces.IEditUserPresenter
import com.example.audiovisualmanager.helper.Constants
import com.example.audiovisualmanager.helper.Utils
import com.example.audiovisualmanager.view.interfaces.IEditUserActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditUserActivity: AppCompatActivity(), IEditUserActivity {
    private lateinit var binding: EdituserActivityBinding
    private var userId: Int = 0
    private var presenter: IEditUserPresenter = EditUserPresenter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EdituserActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter.attachView(this)

        loadViews()
    }

    @Override
    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    private fun loadViews() {
        setupPrivateSpinner()
        if(intent.hasExtra("USERID")){
            userId=intent.getIntExtra("USERID", 0)
        }

        binding.buttonUpdatePassword.setOnClickListener {
            if (binding.editPasswordUpdate.text.toString().isNotEmpty()
                && binding.editPasswordUpdate.text.toString() == binding.editPasswordUpdateRepeat.text.toString()) {

                showLoadingScreen(true)
                lifecycleScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.IO) {
                        presenter.editUser(userId, binding.editPasswordUpdate.text.toString(),
                            binding.spinnerUpdatePrivate.selectedItemPosition == 1)
                    }
                }.invokeOnCompletion { showLoadingScreen(false) }

            } else {
                Toast.makeText(this, getString(R.string.password_dont_match), Toast.LENGTH_SHORT).show()
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
            val intent2= Intent (this , UserListActivity::class.java)
            intent2.putExtra("USERID", userId)
            startActivity(intent2)
        }
    }

    private fun showLoadingScreen(visibleLoading: Boolean) {
        if (visibleLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.lytMainContent.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.lytMainContent.visibility = View.VISIBLE
        }
    }

    private fun setupPrivateSpinner() {
        binding.spinnerUpdatePrivate.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.user_status_array))
    }

    override fun connectionError() {
        Utils.connectionError(this)
    }

    override fun updateUserSuccess() {
        Toast.makeText(this, getString(R.string.password_updated), Toast.LENGTH_SHORT).show()
        val intent2= Intent (this , MainListActivity::class.java)
        intent2.putExtra("USERID", userId)
        startActivity(intent2)
        finish()
    }

    @Override
    override fun onBackPressed() {
        val intent2= Intent (this , MainListActivity::class.java)
        intent2.putExtra("USERID", userId)
        startActivity(intent2)
        finish()
    }
}