package com.example.adegadobiss.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adegadobiss.R
import com.example.adegadobiss.constants.Database
import com.example.adegadobiss.model.AppGlideModules
import com.example.adegadobiss.model.Product
import kotlinx.android.synthetic.main.item_order.view.*
import java.text.DecimalFormat

data class OrderListAdapter(
    val context: Context,
    val productsListDatabase: List<Product>,
    val onItemClickListener: ((product: Product) -> Unit)
) : RecyclerView.Adapter<OrderListAdapter.MyViewHolder>() {
    inner class MyViewHolder(
        itemview: View,
        private val onItemClickListener: ((product: Product) -> Unit)
    ) : RecyclerView.ViewHolder(itemview) {
        fun bindView(product: Product) {
            val decimalFormat = DecimalFormat("0.00")
            itemView.textView10.text = (product.produto.toString())
            itemView.textView11.text = ("Quantidade: " + product.quantidade.toInt().toString())
            itemView.textView12.text =
                ("Valor Unitário: " + "R$ " + decimalFormat.format(product.valor.toDouble()))
            itemView.textView13.text =
                ("Total dos Produtos: " + "R$ " + decimalFormat.format(product.subtotal.toDouble()))

            AppGlideModules().apply {
                Glide.with(context)
                    .load(product.imagem)
                    .centerCrop()
                    .error(R.drawable.beer)
                    .placeholder(R.drawable.beer)
                    .into(itemView.imageViewItemOrder)
            }
            itemView.imageView6.setOnClickListener {
                onItemClickListener.invoke(product)
            }
        }

        fun sumItens(product: Product) {
            val quantity = Database.dbProducts.map { it.subtotal }.sum()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return MyViewHolder(itemview = view, onItemClickListener = onItemClickListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(product = productsListDatabase[position])
        holder.sumItens(product = productsListDatabase[position])
    }

    override fun getItemCount(): Int {
        return productsListDatabase.size
    }
}