package com.example.audiovisualmanager.view

import com.example.audiovisualmanager.R
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.audiovisualmanager.adapter.UserAdapter
import com.example.audiovisualmanager.databinding.UserlistActivityBinding
import com.example.audiovisualmanager.model.User
import com.example.audiovisualmanager.presenter.UserListPresenter
import com.example.audiovisualmanager.presenter.interfaces.IUserListPresenter
import com.example.audiovisualmanager.utils.Constants
import com.example.audiovisualmanager.utils.Utils
import com.example.audiovisualmanager.view.interfaces.IUserListActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class UserListActivity : AppCompatActivity(), IUserListActivity {
    private lateinit var binding: UserlistActivityBinding
    private lateinit var listDataAdapter: ArrayList<User>
    private lateinit var listDataFullAdapter: ArrayList<User>
    private lateinit var adapter: UserAdapter
    var userId: Int = 0
    private var presenter: IUserListPresenter = UserListPresenter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserlistActivityBinding .inflate(layoutInflater)
        setContentView(binding.root)
        presenter.attachView(this)

        if(intent.hasExtra("USERID")){
            userId=intent.getIntExtra("USERID", 0)
        }
        setupAdapter()
    }

    @Override
    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

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
                    Constants.ITEM_PUBLIC -> {
                        filterOnly(Constants.ITEM_PUBLIC)
                    }
                    Constants.ITEM_PRIVATE -> {
                        filterOnly(Constants.ITEM_PRIVATE)
                    }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }
    }

    private fun filterOnly(filter: Int) {
        listDataAdapter.clear()
        if (filter != 0) {
            when (filter) {
                Constants.ITEM_PUBLIC -> {
                    listDataAdapter.addAll(listDataFullAdapter.filter { it.private == 0 })
                }
                Constants.ITEM_PRIVATE -> {
                    listDataAdapter.addAll(listDataFullAdapter.filter { it.private == 1 })
                }
            }
        } else {
            listDataAdapter.addAll(listDataFullAdapter)
        }
        adapter.notifyDataSetChanged()
    }

    private fun setupAdapter() {
        //showLoadingScreen(true)
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                presenter.getUserList(userId)
            }
        }.invokeOnCompletion {
            //showLoadingScreen(false)
        }
    }

    override fun loadUserList(userList: ArrayList<User>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )
        binding.recyclerView.setHasFixedSize(true)
        listDataAdapter = ArrayList<User>()
        listDataFullAdapter = ArrayList<User>()
        listDataFullAdapter.addAll(userList)
        listDataAdapter.addAll(listDataFullAdapter)
        adapter = UserAdapter(listDataAdapter)
        binding.recyclerView.adapter = adapter
        setupUpStatusSpinner()
    }

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

    override fun connectionError() {
        Utils.connectionError(this)
    }
}