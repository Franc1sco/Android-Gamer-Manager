package com.example.audiovisualmanager.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.audiovisualmanager.databinding.ItemListBinding
import com.example.audiovisualmanager.model.Game

class GameAdapter(listData: ArrayList<Game>) :
    RecyclerView.Adapter<GameAdapter.ViewHolderDatos>() {
    var listData: ArrayList<Game> = listData
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDatos {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemListBinding: ItemListBinding =
            ItemListBinding.inflate(layoutInflater, parent, false)
        return ViewHolderDatos(itemListBinding)
    }

    override fun onBindViewHolder(holder: ViewHolderDatos, position: Int) {
        val game: Game = listData[position]
        holder.itemListBinding.game = game
        holder.itemListBinding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    class ViewHolderDatos(itemListBinding: ItemListBinding) :
        RecyclerView.ViewHolder(itemListBinding.root) {
        var itemListBinding: ItemListBinding = itemListBinding

    }

}
