package com.example.adegadobiss.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adegadobiss.R
import com.example.adegadobiss.adapter.OrderHistoryAdapter
import com.example.adegadobiss.adapter.TimestampListAdapter
import com.example.adegadobiss.model.Orders
import com.example.adegadobiss.model.Product
import com.example.adegadobiss.network.FirebaseRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.activity_order_history.*
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.activity_register_user.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.*

class OrderHistoryActivity : AppCompatActivity() {
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private val ordersProducts = mutableListOf<Product>()
    private val date = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        FirebaseCrashlytics.getInstance().log("Init")
        FirebaseCrashlytics.getInstance().sendUnsentReports()

        val crash = FirebaseCrashlytics.getInstance()
        crash.recordException(Throwable())
        observerInput(editTextTextPersonName5)
        bindButtons(imageView12)
    }

    private fun observerInput(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                ordersProducts.clear()
                clearAdapter()
                if (s.toString().length >= 8) {
                    customObjects(s.toString())
                }
            }
        })
    }

    private fun customObjects(telefone: String) {
        val docRef =
            FirebaseRepository.orderCollectionReference.whereEqualTo("telefone", telefone)
        docRef.get().addOnSuccessListener { documentSnapshot ->

            if (documentSnapshot.size() > 0) {
                Toast.makeText(
                    this@OrderHistoryActivity,
                    "Selecione a data do seu pedido",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (documentSnapshot.size() <= 0 && telefone.length >= 9) {
                Toast.makeText(
                    this@OrderHistoryActivity,
                    "Pedido não encontrado, verifique o número de Telefone!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val city = documentSnapshot.documents.map { it ->
                val order = it.toObject<Orders>()
                date.add(order?.data.toString())
                if (order != null) {
                    getOrders(date)
                }
                order?.produtos?.map {
                    ordersProducts.add(it)
                }

            }
        }
    }

    private fun bindButtons(imageViewBack: ImageView) {
        imageViewBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun clearAdapter() {
        ordersProducts.clear()
        val mutableList = mutableListOf<Product>()
        viewAdapter = OrderHistoryAdapter(
            this,
            mutableList
        )
        recyclerViewHistory.apply {
            adapter = viewAdapter
            layoutManager = LinearLayoutManager(
                this@OrderHistoryActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
        }

    }

    private fun getOrders(mutableList: MutableList<String>) {
        recyclerView3.apply {
            viewAdapter = TimestampListAdapter(
                context = this@OrderHistoryActivity,
                mutableList,
            ) { date ->
                ordersProducts.clear()
                setAdapter(date)
            }
            adapter = viewAdapter
            layoutManager = LinearLayoutManager(
                this@OrderHistoryActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }

    }

    private fun setAdapter(data: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val orders =
                FirebaseRepository.orderCollectionReference.whereEqualTo("data", data).get().await()
            for (items in orders) {
                val getOrders = items.toObject<Orders>()
                getOrders.produtos.map {
                    ordersProducts.add(it)
                }
            }
            withContext(Dispatchers.Main) {
                recyclerViewHistory.apply {
                    viewAdapter = OrderHistoryAdapter(
                        context = this@OrderHistoryActivity,
                        ordersProducts
                    )
                    adapter = viewAdapter
                    layoutManager = LinearLayoutManager(this@OrderHistoryActivity)
                }
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@OrderHistoryActivity, e.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}