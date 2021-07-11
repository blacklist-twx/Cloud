package com.example.forlove.Forlove.View

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.forlove.R

/**
 * @data on 2020/9/25 9:05 AM
 * @auther armStrong
 * @describe Recycler使用
 */
class RecyclerAdapter(private val textList: ArrayList<String?>) :
    RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycleritem, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = textList.size ?: 0

    /**
     * function:Item的点击事件*/
    override fun onBindViewHolder(holder: RecyclerAdapter.MyViewHolder, position: Int) {
        val textpos = textList[position]
        holder.title.text = textpos
        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, "${holder.title.text}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.file_name)
    }
}