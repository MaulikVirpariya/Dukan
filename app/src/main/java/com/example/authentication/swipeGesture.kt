package com.example.authentication


import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

abstract class swipeGesture(context: Context) : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
    val deletecolor = ContextCompat.getColor(context,R.color.red)
    val deleteicon= R.drawable.ic_baseline_delete_sweep_24

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        RecyclerViewSwipeDecorator.Builder(
            c,recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
        ).addSwipeLeftBackgroundColor(deletecolor)
            .addSwipeLeftActionIcon(deletecolor)
            .create()
            .decorate()
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

    }
}