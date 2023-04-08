package com.example.adegadobiss.network

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirebaseRepository {
    val personCollectionReference = Firebase.firestore.collection("Clientes")
    val productCollectionReference = Firebase.firestore.collection("Products")
    val orderCollectionReference = Firebase.firestore.collection("Pedidos")
    val CardsCollectionReference = Firebase.firestore.collection("Cartoes")
    val CategoriesCollectionReference = Firebase.firestore.collection("Categorias")
    val deliveryCollectionReference = Firebase.firestore.collection("Entregas")
    val infoCollectionReference = Firebase.firestore.collection("Info")
    val crashlytics = FirebaseCrashlytics.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()
}