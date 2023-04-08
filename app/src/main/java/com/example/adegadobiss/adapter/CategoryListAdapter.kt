package com.example.adegadobiss.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adegadobiss.R
import com.example.adegadobiss.model.Category
import com.example.adegadobiss.adapter.CategoryListAdapter.MyViewHolder
import kotlinx.android.synthetic.main.row_item_product.view.textViewCategory

data class CategoryListAdapter(
    val context: Context,
    var productsListCategories: MutableList<Category>,
    val onItemClickListener: ((category: Category) -> Unit)
) : RecyclerView.Adapter<MyViewHolder>() {
    inner class MyViewHolder(
        itemView: View,
        private val onItemClickListener: (category: Category) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        fun bindView(category: Category) {
            itemView.textViewCategory.text = category.category
           // itemView.imageView.setImageResource(category.image)
            itemView.setOnClickListener {
                onItemClickListener.invoke(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_category, parent, false)
        return MyViewHolder(itemView = view, onItemClickListener = onItemClickListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(category = productsListCategories[position])
    }

    override fun getItemCount(): Int {
        return productsListCategories.size
    }
}


