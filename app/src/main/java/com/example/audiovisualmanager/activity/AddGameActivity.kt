package com.example.audiovisualmanager.activity

import com.example.audiovisualmanager.R
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.audiovisualmanager.database.MysqlManager
import com.example.audiovisualmanager.databinding.AddgameActivityBinding
import com.example.audiovisualmanager.model.Game
import com.example.audiovisualmanager.utils.Constants


class AddGameActivity: AppCompatActivity() {
    private lateinit var binding: AddgameActivityBinding
    private var userId: Int = 0
    private var gameId: Int = 0
    private var dbHandler: MysqlManager = MysqlManager().getInstance()
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
        if (gameId > 0) loadEditGame()

        binding.buttonDoRegister.setOnClickListener{
            if (binding.editTextGameName.text.isEmpty()) {
                Toast.makeText(this, "Please enter a game name", Toast.LENGTH_SHORT).show()
            } else if (binding.spinnerStatus.selectedItemPosition <= 0) {
                Toast.makeText(this, "Please select a status", Toast.LENGTH_SHORT).show()
            } else if (binding.spinnerPlatform.selectedItemPosition <= 0) {
                Toast.makeText(this, "Please select a platform", Toast.LENGTH_SHORT).show()
            } else {

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

    private fun addGame() {
        dbHandler.addGameByUserid(Game(
            binding.editTextGameName.text.toString(),
            binding.spinnerStatus.selectedItem.toString(),
            binding.spinnerPlatform.selectedItem.toString(), 0),
            userId
        )
    }

    private fun updateGame() {
        dbHandler.updateGame(
            Game(
                binding.editTextGameName.text.toString(),
                binding.spinnerStatus.selectedItem.toString(),
                binding.spinnerPlatform.selectedItem.toString(), gameId)
        )
    }

    private fun loadEditGame() {
        val game = dbHandler.getGameById(gameId)
        binding.editTextGameName.setText(game.name)
        binding.spinnerStatus.setSelection(getIndexStatus(binding.spinnerStatus, game.status))
        binding.spinnerPlatform.setSelection(getIndexPlatform(binding.spinnerPlatform, game.platform))
        binding.buttonDoRegister.text = "Actualizar Juego"
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
