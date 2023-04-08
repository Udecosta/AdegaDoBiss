package com.example.adegadobiss.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide.with
import com.example.adegadobiss.R
import com.example.adegadobiss.model.AppGlideModules
import com.example.adegadobiss.model.Product
import kotlinx.android.synthetic.main.row_item_product.view.*
import java.text.DecimalFormat


data class  ProductsListAdapter(
    val context: Context,
    var productsListItems: List<Product>,
    val onItemClickListener: ((product: Product) -> Unit)
) :
    RecyclerView.Adapter<ProductsListAdapter.MyViewHolder>() {

    inner class MyViewHolder(
        itemView: View,
        private val onItemClickListener: ((product: Product) -> Unit)
    ) : RecyclerView.ViewHolder(itemView) {
        fun bindView(product: Product) {
            val decimalFormat = DecimalFormat("0.00")
            itemView.textViewCategory.text = product.produto.toString()
            itemView.textView7.text = ("R$ " + decimalFormat.format(product.valor.toDouble()))

            AppGlideModules().apply {
                    with(context)
                    .load(product.imagem)
                    .centerCrop()
                    .error(R.drawable.ic_launcher_foreground)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(itemView.imageView7)
            }
            itemView.setOnClickListener {
                onItemClickListener.invoke(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_product, parent, false)
        return MyViewHolder(itemView = view, onItemClickListener = onItemClickListener)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(product = productsListItems[position])
    }

    override fun getItemCount(): Int {
        return productsListItems.size
    }

}