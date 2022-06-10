package com.example.audiovisualmanager.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
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
        loadImageSelector()
        if (gameId > 0) loadEditGame()

        binding.buttonDoRegister.setOnClickListener{
            when {
                binding.editTextGameName.text.isEmpty() -> {
                    Toast.makeText(this, "Please enter a game name", Toast.LENGTH_SHORT).show()
                }
                binding.spinnerStatus.selectedItemPosition <= 0 -> {
                    Toast.makeText(this, "Please select a status", Toast.LENGTH_SHORT).show()
                }
                binding.spinnerPlatform.selectedItemPosition <= 0 -> {
                    Toast.makeText(this, "Please select a platform", Toast.LENGTH_SHORT).show()
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
        binding.buttonLoadImage.setOnClickListener(View.OnClickListener {
            if (binding.editTextGameImage.text.isEmpty()) {
                Toast.makeText(this, "Please enter a game image", Toast.LENGTH_SHORT).show()
            } else {
               Glide.with(this).load(binding.editTextGameImage.text.toString()).addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?,
                                              target: com.bumptech.glide.request.target.Target<Drawable>?,
                                              isFirstResource: Boolean): Boolean {
                        Toast.makeText(this@AddGameActivity, "Image not found", Toast.LENGTH_SHORT).show()
                        imageLoaded = false
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?,
                                                 model: Any?,
                                                 target: com.bumptech.glide.request.target.Target<Drawable>?,
                                                 dataSource: com.bumptech.glide.load.DataSource?,
                                                 isFirstResource: Boolean): Boolean {
                        imageLoaded = true
                        return false
                    }
                }).into(binding.imageViewGame)
            }
        })
    }

    private fun addGame() {
        var imageUrl: String? = null
        if (binding.editTextGameImage.text.isNotEmpty() && imageLoaded) {
            imageUrl = binding.editTextGameImage.text.toString()
        }
        dbHandler.addGameByUserid(Game(
            binding.editTextGameName.text.toString(),
            binding.spinnerStatus.selectedItem.toString(),
            binding.spinnerPlatform.selectedItem.toString(), 0, imageUrl),
            userId
        )
    }

    private fun updateGame() {
        var imageUrl: String? = null
        if (binding.editTextGameImage.text.isNotEmpty() && imageLoaded) {
            imageUrl = binding.editTextGameImage.text.toString()
        }
        dbHandler.updateGame(
            Game(
                binding.editTextGameName.text.toString(),
                binding.spinnerStatus.selectedItem.toString(),
                binding.spinnerPlatform.selectedItem.toString(), gameId, imageUrl)
        )
    }

    private fun loadEditGame() {
        val game = dbHandler.getGameById(gameId)
        binding.editTextGameName.setText(game.name)
        binding.spinnerStatus.setSelection(getIndexStatus(binding.spinnerStatus, game.status))
        binding.spinnerPlatform.setSelection(getIndexPlatform(binding.spinnerPlatform, game.platform))
        binding.buttonDoRegister.text = "Actualizar Juego"
        if (game.image.isNullOrEmpty().not()) {
            binding.editTextGameImage.setText(game.image)
            Glide.with(this).load(game.image).into(binding.imageViewGame)
        }
    }

    private fun getIndexStatus(spinner: Spinner, status: Any): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == status) {
                return i
            }
        }
        return 0
    }

    private fun getIndexPlatform(spinner: Spinner, platform: Any): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == platform) {
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

    @Override
    override fun onBackPressed() {
        val intent2= Intent (this ,MainListActivity::class.java)
        intent2.putExtra("USERID", userId)
        startActivity(intent2)
        finish()
    }
}
