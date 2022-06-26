package com.example.audiovisualmanager.view

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.audiovisualmanager.R
import com.example.audiovisualmanager.databinding.AddgameActivityBinding
import com.example.audiovisualmanager.model.Game
import com.example.audiovisualmanager.presenter.AddGamePresenter
import com.example.audiovisualmanager.presenter.interfaces.IAddGamePresenter
import com.example.audiovisualmanager.helper.Utils
import com.example.audiovisualmanager.view.interfaces.IAddGameActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AddGameActivity: AppCompatActivity(), IAddGameActivity {
    private lateinit var binding: AddgameActivityBinding
    private var userId: Int = 0
    private var gameId: Int = 0
    private var presenter: IAddGamePresenter = AddGamePresenter()
    private var imageLoaded = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddgameActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter.attachView(this)

        if(intent.hasExtra("USERID")) {
            userId=intent.getIntExtra("USERID", 0)
        }
        if(intent.hasExtra("GAMEID")) {
            gameId=intent.getIntExtra("GAMEID", 0)
        }

        loadStatusSpinner()
        loadPlatformSpinner()
        loadPointsSpinner()
        loadImageSelector()
        if (gameId > 0) loadEditGame()

        binding.buttonDoRegister.setOnClickListener{
            when {
                binding.editTextGameName.text.isEmpty() -> {
                    Utils.showMessage(this, getString(R.string.error_game_name_empty))
                }
                binding.spinnerStatus.selectedItemPosition <= 0 -> {
                    Utils.showMessage(this, getString(R.string.status_select))
                }
                binding.spinnerPlatform.selectedItemPosition <= 0 -> {
                    Utils.showMessage(this, getString(R.string.select_platform))
                }
                binding.editTextGameCompany.text.isEmpty() -> {
                    Utils.showMessage(this, getString(R.string.select_company))
                }
                binding.editTextGameGenre.text.isEmpty() -> {
                    Utils.showMessage(this, getString(R.string.select_genre))
                }
                else -> {
                    if (gameId > 0) {
                        updateGame()
                    } else {
                        addGame()
                    }
                    val intent2= Intent (this , MainListActivity::class.java)
                    intent2.putExtra("USERID", userId)
                    startActivity(intent2)
                    finish()

                }
            }

        }
    }

    @Override
    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    private fun loadImageSelector() {
        binding.buttonLoadImage.setOnClickListener {
            if (binding.editTextGameImage.text.isEmpty()) {
                Toast.makeText(this, getString(R.string.introducir_imagen), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Glide.with(this).load(binding.editTextGameImage.text.toString())
                    .addListener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?, model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Toast.makeText(
                                this@AddGameActivity,
                                getString(R.string.no_imagen),
                                Toast.LENGTH_SHORT
                            ).show()
                            imageLoaded = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            dataSource: com.bumptech.glide.load.DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            imageLoaded = true
                            return false
                        }
                    }).into(binding.imageViewGame)
            }
        }
    }

    private fun addGame() {
        var imageUrl: String? = null
        if (binding.editTextGameImage.text.isNotEmpty() && imageLoaded) {
            imageUrl = binding.editTextGameImage.text.toString()
        }
        showLoadingScreen(true)
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                presenter.addGameByUserid(Game(
                    binding.editTextGameName.text.toString(),
                    binding.spinnerStatus.selectedItem.toString(),
                    binding.spinnerPlatform.selectedItem.toString(), 0,
                    binding.editTextGameCompany.text.toString(),
                    binding.editTextGameGenre.text.toString(),
                    binding.spinnerPoints.selectedItem.toString().toInt(),
                    imageUrl),
                    userId
                )
            }
        }.invokeOnCompletion { showLoadingScreen(false) }
    }

    private fun updateGame() {
        var imageUrl: String? = null
        if (binding.editTextGameImage.text.isNotEmpty() && imageLoaded) {
            imageUrl = binding.editTextGameImage.text.toString()
        }
        showLoadingScreen(true)
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                presenter.updateGame(Game(
                    binding.editTextGameName.text.toString(),
                    binding.spinnerStatus.selectedItem.toString(),
                    binding.spinnerPlatform.selectedItem.toString(), gameId,
                    binding.editTextGameCompany.text.toString(),
                    binding.editTextGameGenre.text.toString(),
                    binding.spinnerPoints.selectedItem.toString().toInt(),
                    imageUrl))
            }
        }.invokeOnCompletion { showLoadingScreen(false) }
    }

    private fun loadEditGame() {
        showLoadingScreen(true)
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                presenter.getGameById(gameId)
            }
        }.invokeOnCompletion { showLoadingScreen(false) }
    }

    private fun getIndexSpinner(spinner: Spinner, status: Any): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == status) {
                return i
            }
        }
        return 0
    }

    private fun loadStatusSpinner() {
        binding.spinnerStatus.adapter = object : ArrayAdapter<String>(this, R.layout.simple_list_item_1,
            resources.getStringArray(R.array.add_game_array_status)) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val element = view as TextView
                if (position == 0) {
                    element.setTextColor(Color.GRAY)
                } else {
                    element.setTextColor(Color.BLACK)
                }
                return view
            }
        }
    }

    private fun loadPlatformSpinner() {
        binding.spinnerPlatform.adapter = object : ArrayAdapter<String>(this, R.layout.simple_list_item_1,
            resources.getStringArray(R.array.add_game_array_platform)) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val element = view as TextView
                if (position == 0) {
                    element.setTextColor(Color.GRAY)
                } else {
                    element.setTextColor(Color.BLACK)
                }
                return view
            }
        }
    }

    private fun loadPointsSpinner() {
        binding.spinnerPoints.adapter = object : ArrayAdapter<String>(this, R.layout.simple_list_item_1,
            resources.getStringArray(R.array.add_game_array_valoration)) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val element = view as TextView
                if (position == 0) {
                    element.setTextColor(Color.GRAY)
                } else {
                    element.setTextColor(Color.BLACK)
                }
                return view
            }
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

    @Override
    override fun onBackPressed() {
        val intent2= Intent (this , MainListActivity::class.java)
        intent2.putExtra("USERID", userId)
        startActivity(intent2)
        finish()
    }

    override fun connectionError() {
        Utils.connectionError(this)
    }

    override fun updateOrAddGameSuccess() {
        val intent2= Intent (this , MainListActivity::class.java)
        intent2.putExtra("USERID", userId)
        startActivity(intent2)
        finish()
    }

    override fun loadGame(game: Game) {
        binding.editTextGameName.setText(game.name)
        binding.spinnerStatus.setSelection(getIndexSpinner(binding.spinnerStatus, game.status))
        binding.spinnerPlatform.setSelection(getIndexSpinner(binding.spinnerPlatform, game.platform))
        binding.spinnerPoints.setSelection(getIndexSpinner(binding.spinnerPoints, game.valoration.toString()))
        binding.editTextGameCompany.setText(game.company)
        binding.editTextGameGenre.setText(game.genre)
        binding.buttonDoRegister.text = getString(R.string.update_game)
        if (game.image.isNullOrEmpty().not()) {
            binding.editTextGameImage.setText(game.image)
            Glide.with(this).load(game.image).into(binding.imageViewGame)
            imageLoaded = true
        }
    }
}
