package es.ilerna.gamermanager.view

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.ilerna.gamermanager.R
import es.ilerna.gamermanager.databinding.ActivityMainlistBinding
import es.ilerna.gamermanager.model.Game
import es.ilerna.gamermanager.helper.Constants
import es.ilerna.gamermanager.adapter.GameAdapter
import es.ilerna.gamermanager.presenter.interfaces.IMainListPresenter
import es.ilerna.gamermanager.presenter.MainListPresenter
import es.ilerna.gamermanager.helper.SwipeToDelete
import es.ilerna.gamermanager.helper.SwipeToEdit
import es.ilerna.gamermanager.helper.Utils
import es.ilerna.gamermanager.view.interfaces.IMainListActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList

class MainListActivity : AppCompatActivity(), IMainListActivity {
    private lateinit var binding: ActivityMainlistBinding
    private var listDataAdapter = ArrayList<Game>()
    private var listDataFullAdapter = ArrayList<Game>()
    private var adapter: GameAdapter? = null
    private var userId: Int = 0
    private var viewerId: Int = 0
    private var isViewer: Boolean = false
    private var viewerName = ""
    private var isFollowed: Boolean = false
    private var presenter: IMainListPresenter = MainListPresenter()

    // Metodo que se ejecuta al iniciar la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityMainlistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter.attachView(this)

        loadViews()
    }

    // Metodo que carga las vistas
    private fun loadViews() {
        Utils.disallowDarkMode()

        if(intent.hasExtra("USERID")){
            userId=intent.getIntExtra("USERID", 0)
        }

        if(intent.hasExtra("ISVIEWER")){
            isViewer=intent.getBooleanExtra("ISVIEWER", false)
        }
        if(intent.hasExtra("VIEWERNAME")){
            viewerName=intent.getStringExtra("VIEWERNAME") ?: ""
        }
        if(intent.hasExtra("ISFOLLOWED")){
            isFollowed=intent.getBooleanExtra("ISFOLLOWED", false)
        }
        if(intent.hasExtra("VIEWERID")){
            viewerId=intent.getIntExtra("VIEWERID", 0)
        }

        // Oculta los botones de agregar y configurar si es un usuario que esta viendo la lista
        if (isViewer) {
            binding.AddGame.visibility = View.INVISIBLE
            binding.AddGame.isEnabled = false
            binding.UserConfig.visibility = View.INVISIBLE
            binding.UserConfig.isEnabled = false
            binding.tvTitle.text = getString(R.string.viewto, viewerName)
            setupFollow(this.isFollowed)
        }
        loadStatusSpinner()
        loadOrderSpinner()
        setupAdapter()
        setupAscListener()
    }

    private fun setupFollow(isFollowed: Boolean) {
        binding.btnFollow.visibility = View.VISIBLE

        // set image based on isFollowed
        binding.btnFollow.setImageResource(if (isFollowed) R.drawable.ic_baseline_person_remove_64 else R.drawable.ic_baseline_person_add_64)
        binding.btnFollow.setOnClickListener {
            if (isFollowed) {
                lifecycleScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.IO) {
                        presenter.unfollowBy(userId, viewerId)
                    }
                }.invokeOnCompletion { showLoadingScreen(false) }
            } else {
                lifecycleScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.IO) {
                        presenter.followBy(userId, viewerId)
                    }
                }.invokeOnCompletion { showLoadingScreen(false) }
            }
        }
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
                        binding.ivOrderAsc.visibility == View.VISIBLE
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
                        binding.ivOrderAsc.visibility == View.VISIBLE
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
                presenter.orderList(userId, position, binding.ivOrderAsc.visibility == View.VISIBLE)
            }
        }.invokeOnCompletion { showLoadingScreen(false) }
    }

    // Metodo para cargar la lista de juegos principal
    private fun loadMainList() {
        showLoadingScreen(true)
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                presenter.orderList(userId, Constants.ITEM_NOMBRE,
                    binding.ivOrderAsc.visibility == View.VISIBLE)
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
        adapter?.notifyDataSetChanged()
    }

    // metodo para mostrar error en caso de que no se pueda cargar la lista de juegos
    override fun connectionError() {
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                Utils.connectionError(this@MainListActivity)
            }
        }.invokeOnCompletion { showLoadingScreen(false) }
    }

    // metodo para cargar el nuevo orden de la lista de juegos
    override fun applyOrderOnView(gameList: ArrayList<Game>) {
        binding.recyclerView.setHasFixedSize(true)

        listDataAdapter.clear()
        listDataFullAdapter.clear()
        listDataFullAdapter.addAll(gameList)
        listDataAdapter.addAll(listDataFullAdapter)

        adapter = GameAdapter(listDataAdapter, context = this)
        binding.recyclerView.adapter = adapter

        presenter.applyStatusFilter(binding.statusList.selectedItemPosition, listDataFullAdapter)
    }

    override fun followStatusChanged(isFollowed: Boolean) {
        Utils.showMessage(this, getString(if (isFollowed) R.string.follow_status_followed else R.string.follow_status_unfollowed))
        setupFollow(isFollowed)
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

    // Llamada al presionar el boton de volver atras que ejecuta la activity principal o a la actividad de lista de usuarios
    @Override
    override fun onBackPressed() {
        if (!isViewer) {
            val intent2 = Intent(this, MainActivity::class.java)
            startActivity(intent2)
        } else {
            val intent2= Intent (this , UserListActivity::class.java)
            intent2.putExtra("USERID", viewerId)
            startActivity(intent2)
        }
        finish()
    }
}