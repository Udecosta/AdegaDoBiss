package com.example.adegadobiss.view

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adegadobiss.R
import com.example.adegadobiss.adapter.OrderListAdapter
import com.example.adegadobiss.constants.Database
import com.example.adegadobiss.model.Product
import kotlinx.android.synthetic.main.activity_order.*
import java.text.DecimalFormat


class OrderActivity : AppCompatActivity() {
    lateinit var alertDialog: AlertDialog
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private val decimalFormat = DecimalFormat("0.00")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        val product = intent.getParcelableExtra<Product>("produto")

        if (product != null) {
            Database.insertProduct(product = product)
        }
        getOrders()
        val imageViewBack = findViewById<ImageView>(R.id.imageView4)
        backActivity(imageViewBack)
        navigateNextScreen(button2)
        addProducts(imageButton)
        setValue()
    }

    private fun getOrders() {
        val listOrders = mutableListOf<Product>()
        for (list in Database.dbProducts) {
            listOrders.add(list)
        }
        viewAdapter = OrderListAdapter(applicationContext, Database.dbProducts) { Item ->
            deleteItem(Item)
            setValue()
            viewAdapter.notifyDataSetChanged()
        }

        recyclerView.apply {
            adapter = viewAdapter
            layoutManager = LinearLayoutManager(this@OrderActivity)
        }
    }

    private fun deleteItem(product: Product) {
        Database.removeProduct(product = product)
    }

    private fun backActivity(imageView: ImageView) {
        imageView.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setValue() {
        var total: Double = 0.00
        for (i in 0 until Database.dbProducts.size) {
            total += Database.dbProducts[i].subtotal
            Database.dbProducts[i].total = total
        }
        textView15.text = ("R$ " + decimalFormat.format(total.toDouble()))

    }

    private fun addProducts(textViewAdd: TextView) {
        textViewAdd.setOnClickListener {
            val intent = Intent(this, ProductsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateNextScreen(buttonNavigate: Button) {
        buttonNavigate.setOnClickListener {
            if (Database.dbProducts.size > 0 && Database.dbProducts[Database.dbProducts.lastIndex].total >= 15.00) {
                val intent = Intent(this, ActivityRegisterUser::class.java)
                startActivity(intent)
            } else if (Database.dbProducts.size <= 0) {
                Toast.makeText(this, "O carrinho está vazio", Toast.LENGTH_SHORT).show()
            } else if (Database.dbProducts[Database.dbProducts.lastIndex].total <= 14.99) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Ops")
                builder.setMessage("Valor minimo do pedido é de R$15.00 Reais")
                builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, click ->  alertDialog.dismiss()})
                alertDialog = builder.create()
                alertDialog.show()
            }
        }
    }
}
