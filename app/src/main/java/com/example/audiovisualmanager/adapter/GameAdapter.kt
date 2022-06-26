package com.example.audiovisualmanager.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.audiovisualmanager.view.AddGameActivity
import com.example.audiovisualmanager.databinding.ItemMainScreenAdapterBinding
import com.example.audiovisualmanager.model.Game

class GameAdapter(private var listData: ArrayList<Game>, var context: Context) :
    RecyclerView.Adapter<GameAdapter.ViewHolderData>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderData {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemListBinding: ItemMainScreenAdapterBinding =
            ItemMainScreenAdapterBinding.inflate(layoutInflater, parent, false)
        return ViewHolderData(itemListBinding)
    }

    override fun onBindViewHolder(holder: ViewHolderData, position: Int) {
        val game: Game = listData[position]
        holder.itemListBinding.tvName.text = game.name
        holder.itemListBinding.tvStatus.text = game.status
        holder.itemListBinding.tvPlatform.text = game.platform
        if (game.image.isNullOrEmpty().not()) Glide.with(context).load(game.image).into(holder.itemListBinding.ivGameImage)

        setupRate(holder, game.valoration)
    }

    private fun setupRate(holder: ViewHolderData, valoration: Int) {
        holder.itemListBinding.ivStar1.setImageResource(
            if (valoration >= 1) android.R.drawable.star_big_on
            else android.R.drawable.star_big_off
        )
        holder.itemListBinding.ivStar2.setImageResource(
            if (valoration >= 2) android.R.drawable.star_big_on
            else android.R.drawable.star_big_off
        )
        holder.itemListBinding.ivStar3.setImageResource(
            if (valoration >= 3) android.R.drawable.star_big_on
            else android.R.drawable.star_big_off
        )
        holder.itemListBinding.ivStar4.setImageResource(
            if (valoration >= 4) android.R.drawable.star_big_on
            else android.R.drawable.star_big_off
        )
        holder.itemListBinding.ivStar5.setImageResource(
            if (valoration >= 5) android.R.drawable.star_big_on
            else android.R.drawable.star_big_off
        )
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    class ViewHolderData(var itemListBinding: ItemMainScreenAdapterBinding) :
        RecyclerView.ViewHolder(itemListBinding.root) {
    }

    fun removeAt(position: Int): Int {
        listData.removeAt(position)
        notifyItemRemoved(position)
        return listData[position].id
    }

    fun notifyEditItem(context: Context, position: Int, userid: Int){
        val intent2= Intent(context, AddGameActivity::class.java)
        intent2.putExtra("GAMEID", listData[position].id)
        intent2.putExtra("USERID", userid)
        startActivity(context, intent2, null)
    }

}
