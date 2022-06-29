package com.example.audiovisualmanager.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.audiovisualmanager.view.MainListActivity
import com.example.audiovisualmanager.databinding.ItemUserlistScreenAdapterBinding
import com.example.audiovisualmanager.model.User

class UserAdapter(private var listData: ArrayList<User>, private val userId: Int) :
    RecyclerView.Adapter<UserAdapter.ViewHolderData>() {

    // el metodo onCreateViewHolder es el que se ejecuta cuando se crea un nuevo viewholder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderData {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemListBinding: ItemUserlistScreenAdapterBinding =
            ItemUserlistScreenAdapterBinding.inflate(layoutInflater, parent, false)
        return ViewHolderData(itemListBinding)
    }

    // el metodo onBindViewHolder es el que se ejecuta cuando se carga un nuevo viewholder y tratamos los datos
    override fun onBindViewHolder(holder: ViewHolderData, position: Int) {
        val user: User = listData[position]
        holder.itemListBinding.tvName.text = user.name
        holder.itemListBinding.tvFollower.visibility = if(user.follower == true) ViewGroup.VISIBLE else ViewGroup.GONE
        holder.itemListBinding.tvFollowed.visibility = if(user.following == true) ViewGroup.VISIBLE else ViewGroup.GONE
        holder.itemListBinding.tvName.setOnClickListener {
            val intent = Intent(it.context, MainListActivity::class.java)
            intent.putExtra("USERID", listData[position].userid)
            intent.putExtra("ISVIEWER",true)
            intent.putExtra("VIEWERNAME",listData[position].name)
            intent.putExtra("VIEWERID", userId)
            intent.putExtra("ISFOLLOWED", listData[position].following)
            it.context.startActivity(intent)
        }
    }

    // el metodo getItemCount es el que se ejecuta cuando se carga el recyclerView
    // y se cuenta cuantos elementos tiene
    override fun getItemCount(): Int {
        return listData.size
    }

    // cargamos la vista xml
    class ViewHolderData(var itemListBinding: ItemUserlistScreenAdapterBinding) :
        RecyclerView.ViewHolder(itemListBinding.root)
}