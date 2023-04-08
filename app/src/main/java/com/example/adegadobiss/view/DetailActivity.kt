package com.example.adegadobiss.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Parcelable
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.adegadobiss.R
import com.example.adegadobiss.constants.Database
import com.example.adegadobiss.model.Product
import com.example.adegadobiss.network.FirebaseRepository
import com.example.adegadobiss.network.FirebaseRepository.crashlytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.textViewBadge
import kotlinx.android.synthetic.main.activity_product.*
import java.text.DecimalFormat


class DetailActivity : AppCompatActivity() {

    private val decimalFormat = DecimalFormat("0.00")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val product = intent.getParcelableExtra<Product>("produto")
        crashlytics.recordException(Throwable())
        crashlytics.log("Init")
        crashlytics.setCrashlyticsCollectionEnabled(true)
        crashlytics.sendUnsentReports()
        Glide.with(this).apply {
            load(product?.imagem)
                .centerCrop()
                .error(R.drawable.ic_launcher_foreground)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(imageViewItemDetail)
        }

        if (product != null) {
            bindComponents(
                product = product,
                imageViewAddDetail,
                imageViewRemoveDetail,
                buttonAddProductDetail,
            )
        }
        bindButtons(imageView8, imageView9)
        if (Database.dbProducts.size <= 0) {
            textViewBadge.visibility = View.INVISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        if (Database.dbProducts.size > 0) {
            textViewBadge.visibility = View.VISIBLE
            textViewBadge.text = Database.dbProducts.size.toString()
        } else {
            textViewBadge.visibility = View.INVISIBLE
        }
    }

    private fun bindComponents(
        product: Product,
        imageViewAdd: ImageView,
        imageViewRem: ImageView,
        buttonAddProduct: Button,
    ) {
        bindTextView(product = product)

        imageViewAdd.setOnClickListener {
            bindButtonAdd(product = product)
        }
        imageViewRem.setOnClickListener {
            bindButtonRemove(product = product)
        }
        buttonAddProduct.setOnClickListener {
            if (product.quantidade > 0) {

                val intent = Intent(this, OrderActivity::class.java)
                intent.putExtra("produto", product)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Informe a quantidade do produto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindTextView(product: Product) {
        textViewProductNameDetail.text = product.produto.toString()
        textViewQuantityDetail.text = product.quantidade.toString()
        textViewValueDetail.text = ("R$ " + decimalFormat.format(product.valor.toDouble()))
        textViewCont.text = ("Conteúdo: " + product.conteudo.toString())
        textViewDesc.text = product.descricao.toString()
        product.subtotal = product.valor * product.quantidade
    }

    private fun bindButtonAdd(product: Product) {
        product.quantidade = product.quantidade + 1
        product.subtotal = product.valor * product.quantidade
        textViewQuantityDetail.text = (product.quantidade.toString())
        textViewValueDetail.text = ("R$ " + decimalFormat.format(product.subtotal.toDouble()))
    }

    private fun bindButtonRemove(product: Product) {
        if (product.quantidade != 1) {
            product.quantidade = product.quantidade - 1
        }
        product.subtotal = product.valor * product.quantidade
        textViewQuantityDetail.text = (product.quantidade.toString())
        textViewValueDetail.text = ("R$ " + decimalFormat.format(product.subtotal.toDouble()))
    }

    private fun bindButtons(imageViewBack: ImageView, imageViewCart: ImageView) {
        imageViewBack.setOnClickListener {
            val intent = Intent(this, ProductsActivity::class.java)
            startActivity(intent)
        }
        imageViewCart.setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            startActivity(intent)
        }
    }
}