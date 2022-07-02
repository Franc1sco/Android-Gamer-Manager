package com.example.audiovisualmanager.view

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.Window
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

    // metodo que se ejecuta al iniciar la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = EdituserActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter.attachView(this)

        loadViews()
    }

    // Método donde se destruye la actividad y se liberan los recursos
    @Override
    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    // Método que carga las vistas de la actividad
    private fun loadViews() {
        Utils.disallowDarkMode(this)
        setupPrivateSpinner()
        if(intent.hasExtra("USERID")){
            userId=intent.getIntExtra("USERID", 0)
        }

        setupClickListeners()
    }

    // Metodo donde se cargan los listeners de los botones de la actividad
    private fun setupClickListeners() {
        // Boton de guardar cambios
        binding.buttonUpdatePassword.setOnClickListener {
            // Se valida que los campos no esten vacios
            if (binding.editPasswordUpdate.text.toString().isNotEmpty()
                && binding.editPasswordUpdate.text.toString() == binding.editPasswordUpdateRepeat.text.toString()) {

                // Se llama al metodo de actualizar contraseña en un hilo diferente al principal con el presenter
                showLoadingScreen(true)
                lifecycleScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.IO) {
                        presenter.editUser(userId, binding.editPasswordUpdate.text.toString())
                    }
                }.invokeOnCompletion { showLoadingScreen(false) }

            } else {
                Toast.makeText(this, getString(R.string.password_dont_match), Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonUpdateStatus.setOnClickListener {
            // Se llama al metodo de actualizar privacidad en un hilo diferente al principal con el presenter
            showLoadingScreen(true)
            lifecycleScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    presenter.updateUserStatus(userId, binding.spinnerUpdatePrivate.selectedItemPosition == 1)
                }
            }.invokeOnCompletion { showLoadingScreen(false) }
        }

        binding.cbShowPasswordUpdate.setOnClickListener {
            // Se cambia el tipo de input de la contraseña
            if (binding.cbShowPasswordUpdate.isChecked) {
                binding.editPasswordUpdate.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.editPasswordUpdateRepeat.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.editPasswordUpdate.inputType = Constants.TYPE_TEXT_VARIATION_PASSWORD
                binding.editPasswordUpdateRepeat.inputType = Constants.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        binding.buttonViewUsers.setOnClickListener {
            // Se llama a la actividad de lista de usuarios
            val intent2= Intent (this , UserListActivity::class.java)
            intent2.putExtra("USERID", userId)
            startActivity(intent2)
        }
    }

    // Metodo que muestra la pantalla de carga
    private fun showLoadingScreen(visibleLoading: Boolean) {
        if (visibleLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.lytMainContent.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.lytMainContent.visibility = View.VISIBLE
        }
    }

    // Metodo que carga el spinner de privacidad del usuario
    private fun setupPrivateSpinner() {
        binding.spinnerUpdatePrivate.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.user_status_array))
    }

    // Metodo que muestra error de conexion
    override fun connectionError() {
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                Utils.connectionError(this@EditUserActivity)
            }
        }.invokeOnCompletion { showLoadingScreen(false) }
    }

    // metodo que se ejecuta al terminar de actualizar la contraseña y se cierra la actividad volviendo a la actividad anterior
    override fun updateUserSuccess() {
        Toast.makeText(this, getString(R.string.password_updated), Toast.LENGTH_SHORT).show()
        val intent2= Intent (this , MainListActivity::class.java)
        intent2.putExtra("USERID", userId)
        startActivity(intent2)
        finish()
    }

    // metodo que se ejecuta al terminar de actualizar la privacidad y se cierra la actividad volviendo a la actividad anterior
    override fun updateUserStatusSuccess() {
        Toast.makeText(this, getString(R.string.status_updated), Toast.LENGTH_SHORT).show()
        val intent2= Intent (this , MainListActivity::class.java)
        intent2.putExtra("USERID", userId)
        startActivity(intent2)
        finish()
    }

    // Metodo que se ejecuta cuando el usuario da al boton de volver de la actividad anterior
    @Override
    override fun onBackPressed() {
        val intent2= Intent (this , MainListActivity::class.java)
        intent2.putExtra("USERID", userId)
        startActivity(intent2)
        finish()
    }
}