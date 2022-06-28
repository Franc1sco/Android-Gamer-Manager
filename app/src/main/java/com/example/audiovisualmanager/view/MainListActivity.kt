package com.example.audiovisualmanager.view

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.audiovisualmanager.R
import com.example.audiovisualmanager.databinding.ActivityMainlistBinding
import com.example.audiovisualmanager.model.Game
import com.example.audiovisualmanager.helper.Constants
import com.example.audiovisualmanager.adapter.GameAdapter
import com.example.audiovisualmanager.presenter.interfaces.IMainListPresenter
import com.example.audiovisualmanager.presenter.MainListPresenter
import com.example.audiovisualmanager.helper.SwipeToDelete
import com.example.audiovisualmanager.helper.SwipeToEdit
import com.example.audiovisualmanager.helper.Utils
import com.example.audiovisualmanager.view.interfaces.IMainListActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList

class MainListActivity : AppCompatActivity(), IMainListActivity {
    private lateinit var binding: ActivityMainlistBinding
    private lateinit var listDataAdapter: ArrayList<Game>
    private lateinit var listDataFullAdapter: ArrayList<Game>
    private lateinit var adapter: GameAdapter
    private var userId: Int = 0
    private var isViewer: Boolean = false
    private var viewerName = ""
    private var presenter: IMainListPresenter = MainListPresenter()

    // Metodo que se ejecuta al iniciar la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainlistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter.attachView(this)

        loadViews()
    }

    // Metodo que carga las vistas
    private fun loadViews() {
        if(intent.hasExtra("USERID")){
            userId=intent.getIntExtra("USERID", 0)
        }

        if(intent.hasExtra("ISVIEWER")){
            isViewer=intent.getBooleanExtra("ISVIEWER", false)
        }
        if(intent.hasExtra("VIEWERNAME")){
            viewerName=intent.getStringExtra("VIEWERNAME") ?: ""
        }

        // Oculta los botones de agregar y configurar si es un usuario que esta viendo la lista
        if (isViewer) {
            binding.AddGame.visibility = View.INVISIBLE
            binding.AddGame.isEnabled = false
            binding.UserConfig.visibility = View.INVISIBLE
            binding.UserConfig.isEnabled = false
            binding.tvTitle.text = getString(R.string.viewto, viewerName)
        }
        loadStatusSpinner()
        loadOrderSpinner()
        setupAdapter()
        setupAscListener()
    }

    // Metodo que carga el listener de la flecha de ordenamiento
    private fun setupAscListener() {
        binding.ivOrderAsc.setOnClickListener{
            // Cambia el icono de la flecha
            binding.ivOrderAsc.visibility = View.GONE
            binding.ivOrderDesc.visibility = View.VISIBLE
            showLoadingScreen(true)
            // Cambia el orden de la lista haciendo llamada a la base de datos para ordenarla
            lifecycleScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    presenter.orderList(
                        userId,
                        binding.orderList.selectedItemPosition,
                        binding.ivOrderAsc.isVisible
                    )
                }
            }.invokeOnCompletion { showLoadingScreen(false) }
        }
        // lo mismo que el anterior pero con otra imagen
        binding.ivOrderDesc.setOnClickListener{
            binding.ivOrderDesc.visibility = View.GONE
            binding.ivOrderAsc.visibility = View.VISIBLE
            showLoadingScreen(true)
            lifecycleScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    presenter.orderList(
                        userId,
                        binding.orderList.selectedItemPosition,
                        binding.ivOrderAsc.isVisible
                    )
                }
            }.invokeOnCompletion { showLoadingScreen(false) }        }
    }

    // Carga el spinner principal con la lista de juegos
    private fun setupAdapter() {
        binding.recyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        loadMainList()

        // si es un usuario que esta viendo la lista, no se puede editar ni borrar
        if (isViewer) return

        setupSwipes()
    }

    // Metodo para cargar los listeners de ordenar, el estado de los juegos y agregar o editar usuario
    private fun setupListeners() {
        binding.statusList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                presenter.applyStatusFilter(position, listDataFullAdapter)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        binding.orderList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                listOrderBy(position)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        binding.AddGame.setOnClickListener{
            // Vamos a la actividad de agregar juego
            val intent2= Intent (this , AddGameActivity::class.java)
            intent2.putExtra("USERID", userId)
            startActivity(intent2)
            finish()
        }

        binding.UserConfig.setOnClickListener{
            // Vamos a la actividad de configurar usuario
            val intent2= Intent (this , EditUserActivity::class.java)
            intent2.putExtra("USERID", userId)
            startActivity(intent2)
            finish()
        }
    }

    // Metodo que configura los gestos de swipe para borrar y editar
    private fun setupSwipes() {
        val editSwipeHandler = object : SwipeToEdit(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.recyclerView.adapter as GameAdapter
                adapter.notifyEditItem(this@MainListActivity, viewHolder.adapterPosition, userId)
                finish()
            }
        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding.recyclerView)

        val deleteSwipeHandler = object : SwipeToDelete(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.recyclerView.adapter as GameAdapter
                val pos= viewHolder.adapterPosition
                val dialog= AlertDialog.Builder(this@MainListActivity)
                    .setTitle(getString(R.string.alert_dialog_delete))
                    .setMessage(getString(R.string.alert_dialog_confirm_delete))
                    .setNegativeButton(getString(R.string.alert_dialog_no)){ view, _ ->
                        Toast.makeText(this@MainListActivity,getString(R.string.alert_dialog_denied_delete), Toast.LENGTH_LONG).show()
                        view.dismiss()
                        binding.recyclerView.layoutManager = LinearLayoutManager(
                            this@MainListActivity,
                            LinearLayoutManager.VERTICAL, false
                        )
                        binding.recyclerView.setHasFixedSize(true)
                    }
                    .setPositiveButton(getString(R.string.alert_dialog_yes)){ view,_ ->
                        val gameId = adapter.removeAt(pos)
                        showLoadingScreen(true)
                        lifecycleScope.launch(Dispatchers.Main) {
                            withContext(Dispatchers.IO) {
                                presenter.removeGame(gameId)
                            }
                        }.invokeOnCompletion {
                            showLoadingScreen(false)
                            Toast.makeText(this@MainListActivity,getString(R.string.alert_dialog_delete_confirmed), Toast.LENGTH_LONG).show()
                        }
                        view.dismiss()
                    }
                    .setCancelable(false)
                    .create()
                dialog.show()
            }
        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    // Metodo para ordenar la lista de juegos
    private fun listOrderBy(position: Int) {
        if (position == 0) {
            return
        }
        showLoadingScreen(true)
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                presenter.orderList(userId, position, binding.ivOrderAsc.isVisible)
            }
        }.invokeOnCompletion { showLoadingScreen(false) }
    }

    // Metodo para cargar la lista de juegos principal
    private fun loadMainList() {
        showLoadingScreen(true)
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                presenter.orderList(userId, Constants.ITEM_NOMBRE, binding.ivOrderAsc.isVisible)
            }
        }.invokeOnCompletion {
            showLoadingScreen(false)
            setupListeners()
        }
    }

    // Metodo para configurar el spinner de orden y hacer que el primer elemento sea gris de titulo
    private fun loadOrderSpinner() {
        binding.orderList.adapter = object : ArrayAdapter<String>(this, R.layout.simple_list_item_1,
            resources.getStringArray(R.array.main_list_array_order)) {
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

    // Metodo para configurar el spinner de estado y hacer que el primer elemento sea gris de titulo
    private fun loadStatusSpinner() {
        binding.statusList.adapter = object : ArrayAdapter<String>(this, R.layout.simple_list_item_1,
            resources.getStringArray(R.array.main_list_array_status)) {
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

    // metodo al destruirse la actividad para cerrar la conexion con el presenter
    @Override
    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    // metodo para aplicar el filtro de estado
    override fun applyFilterOnView(gameList: ArrayList<Game>) {
        listDataAdapter.clear()
        listDataAdapter.addAll(gameList)
        adapter.notifyDataSetChanged()
    }

    // metodo para mostrar error en caso de que no se pueda cargar la lista de juegos
    override fun connectionError() {
        Utils.connectionError(this)
    }

    // metodo para cargar el nuevo orden de la lista de juegos
    override fun applyOrderOnView(gameList: ArrayList<Game>) {
        binding.recyclerView.setHasFixedSize(true)

        listDataAdapter = gameList
        listDataFullAdapter = gameList
        listDataAdapter = ArrayList()
        listDataFullAdapter = ArrayList()
        listDataFullAdapter.addAll(gameList)
        listDataAdapter.addAll(listDataFullAdapter)

        adapter = GameAdapter(listDataAdapter, context = this)
        binding.recyclerView.adapter = adapter

        presenter.applyStatusFilter(binding.statusList.selectedItemPosition, listDataFullAdapter)
    }

    // metodo para mostrar una pantalla de cargando
    private fun showLoadingScreen(visibleLoading: Boolean) {
        if (visibleLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.clEnd.visibility = View.GONE
            binding.clSubTitle.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.clEnd.visibility = View.VISIBLE
            binding.clSubTitle.visibility = View.VISIBLE
        }
    }

    // Llamada al presionar el boton de volver atras que ejecuta la activity principal
    @Override
    override fun onBackPressed() {
        val intent2= Intent (this , MainActivity::class.java)
        startActivity(intent2)
        finish()
    }
}