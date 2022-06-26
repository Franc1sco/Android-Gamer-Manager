package com.example.audiovisualmanager.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.audiovisualmanager.view.MainListActivity
import com.example.audiovisualmanager.databinding.ItemUserlistScreenAdapterBinding
import com.example.audiovisualmanager.model.User

class UserAdapter(private var listData: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolderData>() {
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
                /*Toast.makeText(
                    it.context,
                    "This is private user",
                    Toast.LENGTH_SHORT
                ).show()*/
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
    class ViewHolderData(var itemListBinding: ItemUserlistScreenAdapterBinding) :
        RecyclerView.ViewHolder(itemListBinding.root)
}