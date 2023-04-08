package com.example.adegadobiss.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adegadobiss.R
import kotlinx.android.synthetic.main.row_item_product.view.*

data class TimestampListAdapter(
    val context: Context,
    var productsListCategories: MutableList<String>,
    val onItemClickListener: ((string : String) -> Unit)
) : RecyclerView.Adapter<TimestampListAdapter.MyViewHolder>() {
    inner class MyViewHolder(
        itemView: View,
        private val onItemClickListener: (string : String) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        fun bindView(string: String) {
            itemView.textViewCategory.text = string
            // itemView.imageView.setImageResource(category.image)
            itemView.setOnClickListener {
                onItemClickListener.invoke(string)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_timestamp, parent, false)
        return MyViewHolder(itemView = view, onItemClickListener = onItemClickListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(string = productsListCategories.sortedDescending()[position])
    }

    override fun getItemCount(): Int {
        return productsListCategories.size
    }
}