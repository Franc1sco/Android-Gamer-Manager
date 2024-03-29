package es.ilerna.gamermanager.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import es.ilerna.gamermanager.R
import es.ilerna.gamermanager.view.AddGameActivity
import es.ilerna.gamermanager.databinding.ItemMainScreenAdapterBinding
import es.ilerna.gamermanager.model.Game

class GameAdapter(private var listData: ArrayList<Game>, var context: Context) :
    RecyclerView.Adapter<GameAdapter.ViewHolderData>() {

    // el metodo onCreateViewHolder es el que se ejecuta cuando se crea un nuevo viewholder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderData {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemListBinding: ItemMainScreenAdapterBinding =
            ItemMainScreenAdapterBinding.inflate(layoutInflater, parent, false)
        return ViewHolderData(itemListBinding)
    }

    // el metodo onBindViewHolder es el que se ejecuta cuando se carga un viewholder y es donde se cargan los datos
    override fun onBindViewHolder(holder: ViewHolderData, position: Int) {
        val game: Game = listData[position]
        holder.itemListBinding.tvName.text = game.name
        holder.itemListBinding.tvStatus.text = game.status
        holder.itemListBinding.tvPlatform.text = context.getString(R.string.platform, game.platform)
        holder.itemListBinding.tvGenre.text = context.getString(R.string.genre, game.genre)
        holder.itemListBinding.tvCompany.text = context.getString(R.string.company, game.company)
        if (game.image.isNullOrEmpty().not()) Glide.with(context).load(game.image).into(holder.itemListBinding.ivGameImage)

        setupRate(holder, game.valoration)
    }

    // segun la valoracion se carga la imagen correspondiente
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

    // el metodo getItemCount es el que se ejecuta cuando se necesita saber la cantidad de elementos que tiene el recyclerView
    override fun getItemCount(): Int {
        return listData.size
    }

    // el xml que le cargamos a la vista
    class ViewHolderData(var itemListBinding: ItemMainScreenAdapterBinding) :
        RecyclerView.ViewHolder(itemListBinding.root) {
    }

    // eliminamos un elemento de la lista
    fun removeAt(position: Int): Int {
        val id = listData[position].id
        listData.removeAt(position)
        notifyItemRemoved(position)
        return id
    }

    // cuando se notifica la edicion de un elemento hacemos llamada a otra activity para ello
    fun notifyEditItem(context: Context, position: Int, userid: Int){
        val intent2= Intent(context, AddGameActivity::class.java)
        intent2.putExtra("GAMEID", listData[position].id)
        intent2.putExtra("USERID", userid)
        startActivity(context, intent2, null)
    }

}
