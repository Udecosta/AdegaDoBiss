package com.example.adegadobiss.view

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.adegadobiss.R
import com.example.adegadobiss.constants.Database
import com.example.adegadobiss.constants.UtilsDelivery
import com.example.adegadobiss.model.Delivery
import com.example.adegadobiss.model.Information
import com.example.adegadobiss.network.FirebaseRepository
import com.example.adegadobiss.validation.ValidFields
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {
    lateinit var alertDialog: AlertDialog
    private var informationsList = mutableListOf<Information>()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView35.setOnClickListener {
            showAlert()
        }
        deliveryIsEnabled(
            button_check_login,
            button
        )
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun init() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val info = FirebaseRepository.infoCollectionReference.get().await()
            for (informations in info.documents) {
                val infos = informations.toObject<Information>()
                if (infos != null) {
                    informationsList.add(infos)
                }
            }
            withContext(Dispatchers.Main) {
                Log.d("Success", "Informações encontradas")
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.d("Error", "${e.message}")
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun deliveryIsEnabled(buttonCheck: Button, buttonOrders: Button) {
        buttonCheck.setOnClickListener {
            val currentDate = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            val hour = currentDate.format(formatter)
            val date: LocalDate = LocalDate.now()
            val dayOfWeak: DayOfWeek = date.dayOfWeek

            Database.clearListProducts()

            val isValid =
                UtilsDelivery().calcHourDelivery(dayOfWeak = dayOfWeak.toString(), hour = hour)

            if (isValid) {
                val intent = Intent(this, ProductsActivity::class.java)
                startActivity(intent)
            } else {
                val delivery =
                    FirebaseRepository.deliveryCollectionReference.document("habilitarEntregas")
                GlobalScope.launch(Dispatchers.IO) {
                    delay(1000L)
                    delivery.get()
                    val getDelivery = delivery.get().await().toObject(Delivery::class.java)
                    withContext(Dispatchers.Main) {
                        if (getDelivery != null) {
                            bindComponents(getDelivery.habilitarEntregas)
                        }
                    }
                }
            }
        }
        buttonOrders.setOnClickListener {
            val intent = Intent(this, OrderHistoryActivity::class.java)
            startActivity(intent)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindComponents(
        habilitar: Boolean
    ) {
        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val hour = currentDate.format(formatter)
        val date: LocalDate = LocalDate.now()
        val dayOfWeak: DayOfWeek = date.dayOfWeek

        Database.clearListProducts()

        val isValid =
            UtilsDelivery().calcHourDelivery(dayOfWeak = dayOfWeak.toString(), hour = hour)

        if (isValid || habilitar) {
            val intent = Intent(this, ProductsActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(
                this,
                "Horario não permitido, verifique o horario de delivery",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this@MainActivity, R.style.CustomAlertDialog)
        val view = layoutInflater.inflate(R.layout.alert_dialog_delivery, null)
        val buttonCancell = view.findViewById<Button>(R.id.button5)
        val textView42 = view.findViewById<TextView>(R.id.textView42)
        val textView43 = view.findViewById<TextView>(R.id.textView43)
        val textView44 = view.findViewById<TextView>(R.id.textView44)
        val textView46 = view.findViewById<TextView>(R.id.textView46)
        if (informationsList.size != 0) {
            textView42.text = ("Telefone 1: ${informationsList[0].telefone1}")
            textView43.text = ("Telefone 2: ${informationsList[0].telefone2}")
            textView44.text = ("Email: ${informationsList[0].email}")
            textView46.text = ("Endereço: ${informationsList[0].endereco}")
        }
        buttonCancell.setOnClickListener {
            alertDialog.dismiss()
        }

        builder.setView(view)
        alertDialog = builder.create()
        alertDialog.show()
    }
}
//    private fun bindComponents(buttonCheckLogin: Button) {
//        buttonCheckLogin.setOnClickListener {
//            checkFields()
//
//        }
////        buttonRegisterClient.setOnClickListener {
////            navigateRegisterUser()
////        }
//    }
//
//    private fun checkFields() {
////        if (email.isNotEmpty() && password.isNotEmpty()) {
////            CoroutineScope(Dispatchers.Main).launch {
////                try {
////                    firebaseAuth.signInWithEmailAndPassword(email, password).await()
////                    withContext(Dispatchers.Main) {
////                        queryUser(email = email)
////                    }
////                } catch (e: Exception) {
////                    withContext(Dispatchers.Main) {
////                        Toast.makeText(
////                            this@MainActivity,
////                            "Usuário ou senha incorretos!",
////                            Toast.LENGTH_SHORT
////                        )
////                            .show()
////                        Log.d("Error", e.message.toString())
////
////                    }
////                }
////            }
////        } else {
////            Toast.makeText(this@MainActivity, "Informe email e senha", Toast.LENGTH_LONG).show()
////
////        }
//
//    }
//
//    private fun queryUser(email: String) = CoroutineScope(Dispatchers.IO).launch {
//        try {
//            val query =
//                FirebaseRepository.personCollectionReference.whereEqualTo("email", email)
//                    .get().await()
//
//            query.documents.map {
//                val client = it.toObject<Client>()
//                if (client?.email.equals(email)) {
//                    withContext(Dispatchers.Main) {
//
//                        if (client != null) {
//
//                        }
//
//                    }
//                } else {
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Houve um problema ao encontrar usuário, tente novamente",
//                            Toast.LENGTH_SHORT
//                        ).show()
//
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            withContext(Dispatchers.Main) {
//
//                Toast.makeText(this@MainActivity, e.message.toString(), Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }
//    }
//
//    private fun loginSuccess(client: Client) {
//
//        val intent = Intent(applicationContext, ProductsActivity::class.java).apply {
//            putExtra("cliente", client)
//        }
//        startActivity(intent)
//        Toast.makeText(this, "Olá ${client.nome}", Toast.LENGTH_SHORT).show()
//    }
//
//    private fun navigateRegisterUser() {
//        val intent = Intent(this@MainActivity, ActivityRegisterUser::class.java)
//        startActivity(intent)
//    }
//
//    private fun initAuthentication(button: Button) {
//        button.setOnClickListener {
//            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.web_client_id))
//                .requestEmail()
//                .build()
//            val signInClient = GoogleSignIn.getClient(this, options)
//            signInClient.signInIntent.also {
//                startActivityForResult(it, REQUEST_CODE_SIGN_IN)
//            }
//        }
//    }
//    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
//        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                firebaseAuth.signInWithCredential(credentials).await()
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(
//                        this@MainActivity,
//                        "Login efetuado com Sucesso!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(this@MainActivity, e.message.toString(), Toast.LENGTH_LONG)
//                        .show()
//                    Log.d("Error", e.message.toString())
//                   }
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_CODE_SIGN_IN) {
//            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
//            account?.let {
//                googleAuthForFirebase(it)
//            }
//        }
//    }
//}