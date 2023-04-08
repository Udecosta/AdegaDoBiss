package com.example.adegadobiss.validation

import android.content.Context
import android.widget.Toast

object MessageView {
     fun showMessage(context: Context, fieldName: String) {
       Toast.makeText(context, "o campo $fieldName está vazio", Toast.LENGTH_SHORT).show()
    }
    fun passwordInvalid(context: Context){
        Toast.makeText(context, "As senhas não conferem", Toast.LENGTH_SHORT).show()
    }
}