package com.example.audiovisualmanager.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.audiovisualmanager.R
import com.example.audiovisualmanager.databinding.ActivityMainBinding
import com.example.audiovisualmanager.presenter.interfaces.IMainPresenter
import com.example.audiovisualmanager.presenter.MainPresenter
import com.example.audiovisualmanager.helper.Constants
import com.example.audiovisualmanager.helper.Utils
import com.example.audiovisualmanager.view.interfaces.IMainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), IMainActivity {
    private lateinit var binding: ActivityMainBinding
    private var presenter: IMainPresenter = MainPresenter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter.attachView(this)

        getSavedSession()

        showLoadingScreen(true)
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                presenter.getConnection()
            }
        }.invokeOnCompletion { showLoadingScreen(false) }

        binding.buttonLogin.setOnClickListener {
            showLoadingScreen(true)
            lifecycleScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    presenter.isValidUser(
                        binding.editTextUser.text.toString(),
                        binding.editTextPassword.text.toString()
                    )
                }
            }.invokeOnCompletion { showLoadingScreen(false) }
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

    @Override
    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    override fun connectionError() {
        Utils.connectionError(this)
    }

    override fun checkSavedSession() {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        if (binding.checkBoxSaveSession.isChecked) {
            sharedPref.edit().putString("USER", binding.editTextUser.text.toString()).apply()
            sharedPref.edit().putString("PASSWORD", binding.editTextPassword.text.toString()).apply()
        } else {
            sharedPref.edit().remove("USER").apply()
            sharedPref.edit().remove("PASSWORD").apply()
        }
    }

    override fun invalidUser() {
        Utils.showMessage(this, getString(R.string.invalid_user_pass))
    }

    override fun validUser(userId: Int) {
        val intent = Intent(this, MainListActivity::class.java)
        intent.putExtra("USERID", userId)
        startActivity(intent)
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
}