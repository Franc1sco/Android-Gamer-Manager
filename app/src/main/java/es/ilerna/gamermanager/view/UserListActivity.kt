package es.ilerna.gamermanager.view

import es.ilerna.gamermanager.R
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import es.ilerna.gamermanager.adapter.UserAdapter
import es.ilerna.gamermanager.databinding.UserlistActivityBinding
import es.ilerna.gamermanager.model.User
import es.ilerna.gamermanager.presenter.UserListPresenter
import es.ilerna.gamermanager.presenter.interfaces.IUserListPresenter
import es.ilerna.gamermanager.helper.Constants
import es.ilerna.gamermanager.helper.Utils
import es.ilerna.gamermanager.view.interfaces.IUserListActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class UserListActivity : AppCompatActivity(), IUserListActivity {
    private lateinit var binding: UserlistActivityBinding
    private var listDataAdapter = ArrayList<User>()
    private var listDataFullAdapter = ArrayList<User>()
    private var adapter: UserAdapter? = null
    var userId: Int = 0
    private var presenter: IUserListPresenter = UserListPresenter()

    // metodo que se ejecuta al iniciar la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = UserlistActivityBinding .inflate(layoutInflater)
        setContentView(binding.root)
        presenter.attachView(this)

        loadViews()
    }

    // metodo para cargar los datos en la vista
    private fun loadViews() {
        Utils.disallowDarkMode()

        if(intent.hasExtra("USERID")){
            userId=intent.getIntExtra("USERID", 0)
        }
        setupAdapter()
    }

    // metodo donde se destruye la actividad y se liberan los recursos
    @Override
    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    // metodo para cargar los datos del spinner de estado para filtrar los usuarios
    private fun setupUpStatusSpinner() {
        binding.statusList.adapter =
            ArrayAdapter(this, R.layout.simple_list_item_1, resources.getStringArray(R.array.user_status_array_all))

        binding.statusList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        filterOnly(0)
                    }
                    Constants.ITEM_FOLLOWERS -> {
                        filterOnly(Constants.ITEM_FOLLOWERS)
                    }
                    Constants.ITEM_FOLLOWING -> {
                        filterOnly(Constants.ITEM_FOLLOWING)
                    }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }
    }

    // metodo que se usa para aplicar el filtro de estado
    private fun filterOnly(filter: Int) {
        listDataAdapter.clear()
        if (filter != 0) {
            when (filter) {
                Constants.ITEM_FOLLOWERS -> {
                    listDataAdapter.addAll(listDataFullAdapter.filter { it.follower == true })
                }
                Constants.ITEM_FOLLOWING -> {
                    listDataAdapter.addAll(listDataFullAdapter.filter { it.following == true })
                }
            }
        } else {
            listDataAdapter.addAll(listDataFullAdapter)
        }
        adapter?.notifyDataSetChanged()
    }

    // metodo para cargar los datos del recycler view de usuarios haciendo llamada a la base de datos desde el presenter
    private fun setupAdapter() {
        showLoadingScreen(true)
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                presenter.getUserList(userId)
            }
        }.invokeOnCompletion { showLoadingScreen(false) }
    }

    // metodo que se ejecuta cuando carga la lista de usuarios desde la base de datos en el presenter
    override fun loadUserList(userList: ArrayList<User>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        binding.recyclerView.setHasFixedSize(true)
        listDataAdapter.clear()
        listDataFullAdapter.clear()
        listDataFullAdapter.addAll(userList)
        listDataAdapter.addAll(listDataFullAdapter)
        adapter = UserAdapter(listDataAdapter, this@UserListActivity, userId)
        binding.recyclerView.adapter = adapter
        setupUpStatusSpinner()
    }

    // metodo que se ejecuta cuando se muestra el loading screen
    private fun showLoadingScreen(visibleLoading: Boolean) {
        if (visibleLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
            binding.clSubTitle.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.clSubTitle.visibility = View.VISIBLE
        }
    }

    // metodo que se ejecuta cuando se muestra un mensaje de error de conexion
    override fun connectionError() {
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                Utils.connectionError(this@UserListActivity)
            }
        }.invokeOnCompletion { showLoadingScreen(false) }
    }
}