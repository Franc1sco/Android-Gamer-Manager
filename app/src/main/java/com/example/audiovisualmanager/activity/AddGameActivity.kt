package com.example.audiovisualmanager.activity

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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.audiovisualmanager.R
import com.example.audiovisualmanager.database.MysqlManager
import com.example.audiovisualmanager.databinding.AddgameActivityBinding
import com.example.audiovisualmanager.model.Game
import com.example.audiovisualmanager.utils.Constants
import com.example.audiovisualmanager.utils.Utils


class AddGameActivity: AppCompatActivity() {
    private lateinit var binding: AddgameActivityBinding
    private var userId: Int = 0
    private var gameId: Int = 0
    private var dbHandler: MysqlManager = MysqlManager().getInstance()
    private var imageLoaded = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddgameActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("USERID")){
            userId=intent.getIntExtra("USERID", 0)
        }
        if(intent.hasExtra("GAMEID")){
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
                    Utils.showMessage(this, "Please select a status")
                }
                binding.spinnerPlatform.selectedItemPosition <= 0 -> {
                    Utils.showMessage(this, "Please select a platform")
                }
                binding.editTextGameCompany.text.isEmpty() -> {
                    Utils.showMessage(this, "Please enter a Company name")
                }
                binding.editTextGameGenre.text.isEmpty() -> {
                    Utils.showMessage(this, "Please enter a Genre name")
                }
                else -> {
                    if (gameId > 0) {
                        updateGame()
                    } else {
                        addGame()
                    }
                    val intent2= Intent (this ,MainListActivity::class.java)
                    intent2.putExtra("USERID", userId)
                    startActivity(intent2)
                    finish()

                }
            }

        }
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
        if (!dbHandler.addGameByUserid(Game(
            binding.editTextGameName.text.toString(),
            binding.spinnerStatus.selectedItem.toString(),
            binding.spinnerPlatform.selectedItem.toString(), 0,
            binding.editTextGameCompany.text.toString(),
            binding.editTextGameGenre.text.toString(),
            binding.spinnerPoints.selectedItem.toString().toInt(),
            imageUrl),
            userId
        )) {
            Utils.connectionError(this)
        }
    }

    private fun updateGame() {
        var imageUrl: String? = null
        if (binding.editTextGameImage.text.isNotEmpty() && imageLoaded) {
            imageUrl = binding.editTextGameImage.text.toString()
        }
        if (!dbHandler.updateGame(
            Game(
                binding.editTextGameName.text.toString(),
                binding.spinnerStatus.selectedItem.toString(),
                binding.spinnerPlatform.selectedItem.toString(), gameId,
                binding.editTextGameCompany.text.toString(),
                binding.editTextGameGenre.text.toString(),
                binding.spinnerPoints.selectedItem.toString().toInt(),
                imageUrl)
        )) {
            Utils.connectionError(this)
        }
    }

    private fun loadEditGame() {
        val game = dbHandler.getGameById(gameId)
        if (game == null) {
            Utils.connectionError(this)
            return
        }
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

    private fun getIndexSpinner(spinner: Spinner, status: Any): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == status) {
                return i
            }
        }
        return 0
    }

    private fun loadStatusSpinner() {
        val array = listOf("Status", Constants.PENDIENTE, Constants.EN_PROCESO, Constants.FINALIZADO)
        binding.spinnerStatus.adapter = object : ArrayAdapter<String>(this, R.layout.simple_list_item_1, array) {
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
        val array = listOf("Plataforma", Constants.PC, Constants.PLAYSTATION, Constants.XBOX)
        binding.spinnerPlatform.adapter = object : ArrayAdapter<String>(this, R.layout.simple_list_item_1, array) {
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
        val array = listOf("Valoración", "1", "2", "3", "4", "5")
        binding.spinnerPoints.adapter = object : ArrayAdapter<String>(this, R.layout.simple_list_item_1, array) {
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

    @Override
    override fun onBackPressed() {
        val intent2= Intent (this ,MainListActivity::class.java)
        intent2.putExtra("USERID", userId)
        startActivity(intent2)
        finish()
    }
}
