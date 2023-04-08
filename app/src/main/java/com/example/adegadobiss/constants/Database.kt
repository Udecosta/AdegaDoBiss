package com.example.adegadobiss.constants

import com.example.adegadobiss.model.Client
import com.example.adegadobiss.model.Payment
import com.example.adegadobiss.model.Product



object Database {

    var dbClients = mutableListOf<Client>()
    var dbProducts = mutableListOf<Product>()
    var dbPayment = mutableListOf<Payment>()

    fun insertProduct(product: Product){
       dbProducts.add(product)
    }
    fun removeProduct(product: Product){
        dbProducts.remove(product)
    }
    fun clearListProducts(){
        dbProducts.clear()
    }
    fun insertPayment(payment: Payment){
        dbPayment.add(payment)
    }
    fun listAll(): Int{
        return dbProducts.size
    }
    fun insertClient(client: Client){
        dbClients.add(client)
    }
    fun clearListClients(){
        dbClients.clear()
    }


}