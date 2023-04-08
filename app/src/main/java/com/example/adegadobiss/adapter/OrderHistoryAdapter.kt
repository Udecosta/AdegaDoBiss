package com.example.adegadobiss.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adegadobiss.R
import com.example.adegadobiss.model.AppGlideModules
import com.example.adegadobiss.model.Product
import kotlinx.android.synthetic.main.item_order_history.view.*
import java.text.DecimalFormat

data class OrderHistoryAdapter(
    val context: Context,
    val productsDatabase: List<Product>
) :
    RecyclerView.Adapter<OrderHistoryAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindView(product: Product) {
            val decimalFormat = DecimalFormat("0.00")
            itemView.textView6.text = product.produto.toString()
            itemView.textView28.text =
                ("Quantidade: " + product.quantidade.toString())
            itemView.textView29.text =
                ("Valor Unitário: " + "R$ " + decimalFormat.format(product.valor.toDouble()))
            itemView.textView31.text =
                ("Total dos Produtos: " + "R$ " + decimalFormat.format(product.subtotal.toDouble()))
            AppGlideModules().apply {
                Glide.with(context)
                    .load(product.imagem)
                    .centerCrop()
                    .error(R.drawable.beer)
                    .placeholder(R.drawable.beer)
                    .into(itemView.imageView13)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_order_history, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(product = productsDatabase[position])

    }

    override fun getItemCount(): Int {
        return productsDatabase.size
    }
}
