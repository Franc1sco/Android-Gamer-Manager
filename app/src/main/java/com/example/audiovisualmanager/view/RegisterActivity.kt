package com.example.audiovisualmanager.view

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.audiovisualmanager.R
import com.example.audiovisualmanager.databinding.RegisterActivityBinding
import com.example.audiovisualmanager.model.User
import com.example.audiovisualmanager.presenter.RegisterPresenter
import com.example.audiovisualmanager.presenter.interfaces.IRegisterPresenter
import com.example.audiovisualmanager.helper.Constants
import com.example.audiovisualmanager.helper.Utils
import com.example.audiovisualmanager.view.interfaces.IRegisterActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity(), IRegisterActivity {
    private lateinit var binding: RegisterActivityBinding
    private var presenter: IRegisterPresenter = RegisterPresenter()

    // metodo que se ejecuta al iniciar la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = RegisterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter.attachView(this)

        loadViews()
    }

    // metodo que carga las vistas donde sse fija los listeners y los datos de los campos
    private fun loadViews() {
        Utils.disallowDarkMode(this)

        binding.buttonDoRegister.setOnClickListener {
            val name = binding.editTextUserRegister.text.toString()
            val pass = binding.editTextPasswordRegister.text.toString()
            if (name.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            } else {
                showLoadingScreen(true)
                lifecycleScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.IO) {
                        presenter.checkUserExists(name)
                    }
                }.invokeOnCompletion { showLoadingScreen(false) }
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

    // metodo donde se destruye la actividad y se liberan los recursos
    @Override
    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    // metodo que muestra o oculta el loading screen
    private fun showLoadingScreen(visibleLoading: Boolean) {
        if (visibleLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.lytMainContent.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.lytMainContent.visibility = View.VISIBLE
        }
    }

    // metodo que muestra el toast de error de conexion
    override fun connectionError() {
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                Utils.connectionError(this@RegisterActivity)
            }
        }.invokeOnCompletion { showLoadingScreen(false) }
    }

    // metodo que muestra el toast de error de usuario existente
    override fun showUserExists() {
        Toast.makeText(this, getString(R.string.user_already_extis), Toast.LENGTH_SHORT).show()
    }

    // metodo que agrega un usuario a la base de datos
    override fun showUserDoesNotExist() {
        val name = binding.editTextUserRegister.text.toString()
        val pass = binding.editTextPasswordRegister.text.toString()
        showLoadingScreen(true)
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                presenter.addUser(User(name, pass))
            }
        }.invokeOnCompletion { showLoadingScreen(false) }
    }

    // metodo que muestra el toast de usuario agregado
    override fun userAddedSuccessfully() {
        Utils.showMessage(this, getString(R.string.user_registered_success))
        val intent2= Intent (this , MainActivity::class.java)
        startActivity(intent2)
        finish()
    }

    // Llamada al presionar el boton de volver atras que ejecuta la activity principal
    @Override
    override fun onBackPressed() {
        val intent2= Intent (this , MainActivity::class.java)
        startActivity(intent2)
        finish()
    }
}
