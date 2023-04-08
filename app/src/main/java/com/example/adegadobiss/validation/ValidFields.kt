package com.example.adegadobiss.validation

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.adegadobiss.R
import com.example.adegadobiss.constants.Database
import com.example.adegadobiss.model.Delivery
import com.example.adegadobiss.network.FirebaseRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.security.AccessControlContext
import java.time.DayOfWeek

object ValidFields {
    fun validFields(editText: EditText): Boolean {
        return editText.text.toString().isNotEmpty() && editText.text.toString().isNotBlank()
    }
}