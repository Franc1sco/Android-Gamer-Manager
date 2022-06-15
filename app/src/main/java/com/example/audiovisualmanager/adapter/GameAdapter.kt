package com.example.audiovisualmanager.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.audiovisualmanager.activity.AddGameActivity
import com.example.audiovisualmanager.database.MysqlManager
import com.example.audiovisualmanager.databinding.ItemMainScreenAdapterBinding
import com.example.audiovisualmanager.model.Game

class GameAdapter(listData: ArrayList<Game>, context: Context) :
    RecyclerView.Adapter<GameAdapter.ViewHolderDatos>() {
    var listData: ArrayList<Game> = listData
    var context: Context = context
    private var dbHandler: MysqlManager = MysqlManager().getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDatos {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemListBinding: ItemMainScreenAdapterBinding =
            ItemMainScreenAdapterBinding.inflate(layoutInflater, parent, false)
        return ViewHolderDatos(itemListBinding)
    }

    override fun onBindViewHolder(holder: ViewHolderDatos, position: Int) {
        val game: Game = listData[position]
        holder.itemListBinding.tvName.text = game.name
        holder.itemListBinding.tvStatus.text = game.status
        holder.itemListBinding.tvPlatform.text = game.platform
        if (game.image.isNullOrEmpty().not()) Glide.with(context).load(game.image).into(holder.itemListBinding.ivGameImage)

        setupRate(holder, game.valoration)
    }

    private fun setupRate(holder: GameAdapter.ViewHolderDatos, valoration: Int) {
        when (valoration) {
            1 -> {
                holder.itemListBinding.ivStar1.setImageResource(android.R.drawable.star_big_on)
                holder.itemListBinding.ivStar2.setImageResource(android.R.drawable.star_big_off)
                holder.itemListBinding.ivStar3.setImageResource(android.R.drawable.star_big_off)
                holder.itemListBinding.ivStar4.setImageResource(android.R.drawable.star_big_off)
                holder.itemListBinding.ivStar5.setImageResource(android.R.drawable.star_big_off)
            }
            2 -> {
                holder.itemListBinding.ivStar1.setImageResource(android.R.drawable.star_big_on)
                holder.itemListBinding.ivStar2.setImageResource(android.R.drawable.star_big_on)
                holder.itemListBinding.ivStar3.setImageResource(android.R.drawable.star_big_off)
                holder.itemListBinding.ivStar4.setImageResource(android.R.drawable.star_big_off)
                holder.itemListBinding.ivStar5.setImageResource(android.R.drawable.star_big_off)
            }
            3 -> {
                holder.itemListBinding.ivStar1.setImageResource(android.R.drawable.star_big_on)
                holder.itemListBinding.ivStar2.setImageResource(android.R.drawable.star_big_on)
                holder.itemListBinding.ivStar3.setImageResource(android.R.drawable.star_big_on)
                holder.itemListBinding.ivStar4.setImageResource(android.R.drawable.star_big_off)
                holder.itemListBinding.ivStar5.setImageResource(android.R.drawable.star_big_off)
            }
            4 -> {
                holder.itemListBinding.ivStar1.setImageResource(android.R.drawable.star_big_on)
                holder.itemListBinding.ivStar2.setImageResource(android.R.drawable.star_big_on)
                holder.itemListBinding.ivStar3.setImageResource(android.R.drawable.star_big_on)
                holder.itemListBinding.ivStar4.setImageResource(android.R.drawable.star_big_on)
                holder.itemListBinding.ivStar5.setImageResource(android.R.drawable.star_big_off)
            }
            5 -> {
                holder.itemListBinding.ivStar1.setImageResource(android.R.drawable.star_big_on)
                holder.itemListBinding.ivStar2.setImageResource(android.R.drawable.star_big_on)
                holder.itemListBinding.ivStar3.setImageResource(android.R.drawable.star_big_on)
                holder.itemListBinding.ivStar4.setImageResource(android.R.drawable.star_big_on)
                holder.itemListBinding.ivStar5.setImageResource(android.R.drawable.star_big_on)
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    class ViewHolderDatos(itemListBinding: ItemMainScreenAdapterBinding) :
        RecyclerView.ViewHolder(itemListBinding.root) {
        var itemListBinding: ItemMainScreenAdapterBinding = itemListBinding
    }

    fun removeAt(position: Int){
        dbHandler.deleteGame(listData[position].id)
        listData.removeAt(position)
        notifyItemRemoved(position)
    }

    fun notifyEditItem(context: Context, position: Int, userid: Int){
        val intent2= Intent(context, AddGameActivity::class.java)
        intent2.putExtra("GAMEID", listData[position].id)
        intent2.putExtra("USERID", userid)
        startActivity(context, intent2, null)
    }

}
