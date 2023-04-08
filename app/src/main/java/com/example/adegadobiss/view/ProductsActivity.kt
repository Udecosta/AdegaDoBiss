package com.example.adegadobiss.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adegadobiss.R
import com.example.adegadobiss.adapter.CategoryListAdapter
import com.example.adegadobiss.adapter.ProductsListAdapter
import com.example.adegadobiss.constants.Database
import com.example.adegadobiss.model.Category
import com.example.adegadobiss.model.Product
import com.example.adegadobiss.network.FirebaseRepository
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

class ProductsActivity : AppCompatActivity() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private val listProducts = mutableListOf<Product>()
    private val listCategories = mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        getListProducts()
        searchItem(editTextSearch = editTextSearchItem)
        listCategories.add(Category("Ver Tudo"))
        listCategories.add(Category("Cerveja"))
        listCategories.add(Category("Whisky"))
        listCategories.add(Category("Vodka"))
        listCategories.add(Category("Gin"))
        listCategories.add(Category("Cachaça"))
        listCategories.add(Category("Energeticos"))
        listCategories.add(Category("Refrigerantes"))
        listCategories.add(Category("Tabacaria"))
        listCategories.add(Category("Gelo"))
        listCategories.add(Category("Petiscos"))
        listCategories.add(Category("Combos"))
        listCategories.add(Category("Promoções"))
        selectCategory()
        bindButtons(imageView10, imageButton)
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

    private fun searchItem(editTextSearch: EditText) {
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                sendItemSearch(s.toString())
            }
        })
    }

    private fun sendItemSearch(items: String) {
        val itemSearch = mutableListOf<Product>()
        for (item in listProducts) {
            if (item.produto.toString().toLowerCase(Locale.ROOT).contains(
                    items.toString().toLowerCase(
                        Locale.ROOT
                    )
                )
            ) {
                itemSearch.add(item)
            }
        }
        recycler_firestore.apply {
            viewAdapter = ProductsListAdapter(
                productsListItems = itemSearch,
                context = this@ProductsActivity
            ) { product ->
                navigateNextScreen(product)
            }
            adapter = viewAdapter
            layoutManager = GridLayoutManager(this@ProductsActivity, 2)
        }
    }

    private fun navigateNextScreen(product: Product) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("produto", product)
        }
        startActivity(intent)
    }


    private fun getListProducts() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val getList =
                FirebaseRepository.productCollectionReference.whereEqualTo("estoque", true).get()
                    .await()
            //val listProducts = mutableListOf<Product>()
            for (products in getList) {
                val product = products.toObject<Product>()
                listProducts.add(product)
            }
            withContext(Dispatchers.Main) {
                viewAdapter = ProductsListAdapter(
                    productsListItems = listProducts,
                    context = this@ProductsActivity
                ) { product ->
                    navigateNextScreen(product)
                }
                recycler_firestore.apply {
                    adapter = viewAdapter
                    layoutManager = GridLayoutManager(this@ProductsActivity, 2)
                }
            }
        } catch (e: Exception) {
            Log.d("Error", e.message.toString())
        }
    }


    private fun setAdapter(category: String) {
        val itemSearch = mutableListOf<Product>()
        for (item in listProducts) {
            if (item.categoria.toString().toLowerCase(Locale.ROOT).contains(
                    category.toString().toLowerCase(
                        Locale.ROOT
                    )
                )
            ) {
                itemSearch.add(item)
            }
        }
        recycler_firestore.apply {
            viewAdapter = ProductsListAdapter(
                productsListItems = itemSearch,
                context = this@ProductsActivity
            ) { product ->
                navigateNextScreen(product)
            }
            adapter = viewAdapter
            layoutManager = GridLayoutManager(this@ProductsActivity, 2)
        }
    }

    private fun selectCategory() {
        recyclerView2.apply {
            viewAdapter = CategoryListAdapter(
                productsListCategories = listCategories,
                context = this@ProductsActivity
            ) { category ->
                setAdapter(category.category.toString())
                if (category.category.toString().startsWith("Ver")) {
                    sendItemSearch("")
                }
            }
            adapter = viewAdapter
            layoutManager =
                LinearLayoutManager(this@ProductsActivity, LinearLayoutManager.HORIZONTAL, false)
        }

    }

    private fun bindButtons(buttonBack: ImageView, buttonCart: ImageView) {
        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        buttonCart.setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            startActivity(intent)
        }
    }
}