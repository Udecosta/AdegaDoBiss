package com.example.adegadobiss.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.example.adegadobiss.R
import com.example.adegadobiss.model.Category

class CategoryActivity : AppCompatActivity() {
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        selectCategory()
    }
    private fun selectCategory(){
//        recycler_category.apply {
//            viewAdapter = CategoryListAdapter(
//                productsListCategories = listCategories,
//                context = this@CategoryActivity
//            ) { product ->
//                navigateNextScreen(product)
//            }
//            adapter = viewAdapter
//            layoutManager = GridLayoutManager(this@CategoryActivity, 2)
//        }
    }
    private fun navigateNextScreen(category: Category){
        val intent = Intent(this, ProductsActivity::class.java).apply {
            putExtra("produto", category)
        }
        startActivity(intent)
    }
}