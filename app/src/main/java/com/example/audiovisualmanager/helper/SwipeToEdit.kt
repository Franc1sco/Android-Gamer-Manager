package com.example.audiovisualmanager.helper

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.audiovisualmanager.R

abstract class SwipeToEdit(context: Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
    //Esta clase nos permite desplazar el dedo hacia la derecha y que cambie lo que se muestra

    private val editIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_edit_24)
    private val intrinsicWidth = editIcon?.intrinsicWidth
    private val intrinsicHeight = editIcon?.intrinsicHeight
    private val background = ColorDrawable()
    private val backgroundColor = Color.parseColor("#24AE05")
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    // Llamada cuando el usuario arrastra una vista hacia la izquierda o derecha.
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    //Sobreescribiendo este método mostramos un fondo verde con el icono de editar
    override fun onChildDraw(
            c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(c, itemView.left + dX, itemView.top.toFloat(), itemView.left.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        background.color = backgroundColor
        background.setBounds(itemView.left + dX.toInt(), itemView.top, itemView.left, itemView.bottom)
        background.draw(c)

        if (intrinsicHeight == null || intrinsicWidth == null) return

        val editIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val editIconMargin = (itemHeight - intrinsicHeight)
        val editIconLeft = itemView.left + editIconMargin - intrinsicWidth
        val editIconRight = itemView.left + editIconMargin
        val editIconBottom = editIconTop + intrinsicHeight

        editIcon?.setBounds(editIconLeft, editIconTop, editIconRight, editIconBottom)
        editIcon?.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    // Llamada cuando el usuario suelta el dedo de la pantalla.
    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}