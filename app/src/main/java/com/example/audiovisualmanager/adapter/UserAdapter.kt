package com.example.audiovisualmanager.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.audiovisualmanager.activity.MainListActivity
import com.example.audiovisualmanager.databinding.ItemUserlistScreenAdapterBinding
import com.example.audiovisualmanager.model.User

class UserAdapter(listData: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolderData>() {
    private var listData: ArrayList<User> = listData
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderData {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemListBinding: ItemUserlistScreenAdapterBinding =
            ItemUserlistScreenAdapterBinding.inflate(layoutInflater, parent, false)
        return ViewHolderData(itemListBinding)
    }

    override fun onBindViewHolder(holder: ViewHolderData, position: Int) {
        val user: User = listData[position]
        holder.itemListBinding.tvName.text = user.name
        holder.itemListBinding.tvName.setOnClickListener {
            if (listData[position].private == 1) {
                Toast.makeText(
                    it.context,
                    "This is private user",
                    Toast.LENGTH_SHORT
                ).show()
                // todo poner toast
            } else {
                val intent = Intent(it.context, MainListActivity::class.java)
                intent.putExtra("USERID", listData[position].userid)
                intent.putExtra("ISVIEWER",true)
                intent.putExtra("VIEWERNAME",listData[position].name)
                it.context.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return listData.size
    }
    class ViewHolderData(itemListBinding: ItemUserlistScreenAdapterBinding) :
        RecyclerView.ViewHolder(itemListBinding.root) {
        var itemListBinding: ItemUserlistScreenAdapterBinding = itemListBinding
    }
}