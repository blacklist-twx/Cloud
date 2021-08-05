package com.example.forlove.Forlove.View.MyView

import android.util.Log
import androidx.recyclerview.widget.RecyclerView

open class Scroll_listener: RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        Log.i("move","move")
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        Log.i("move","move")
    }
}