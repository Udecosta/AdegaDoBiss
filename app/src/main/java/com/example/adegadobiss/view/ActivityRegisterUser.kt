package com.example.adegadobiss.view

import android.content.Intent
import android.location.GnssAntennaInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.adegadobiss.R
import com.example.adegadobiss.constants.Constants
import com.example.adegadobiss.constants.Database
import com.example.adegadobiss.constants.UtilsDelivery
import com.example.adegadobiss.model.Client
import com.example.adegadobiss.model.Endereco
import com.example.adegadobiss.model.Frete
import com.example.adegadobiss.network.ConfigService
import com.example.adegadobiss.network.FirebaseRepository
import com.example.adegadobiss.validation.MessageView
import com.example.adegadobiss.validation.ValidFields
import com.github.rtoshiro.util.format.SimpleMaskFormatter
import com.github.rtoshiro.util.format.text.SimpleMaskTextWatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.type.LatLng
import kotlinx.android.synthetic.main.activity_register_user.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.DecimalFormat
import java.util.*
import kotlin.math.sin

class ActivityRegisterUser : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    var userIsValid: Boolean = false
    private val clientRegister = Client()
    private val decimalFormat = DecimalFormat("0.00")
    private val frete: Frete = Frete(0.00)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        firebaseAuth = FirebaseAuth.getInstance()
        searchCep()
        containsUser(editTextPhone)
        buttonRegister.setOnClickListener {
            if (editTextPhone.text.toString()
                    .isEmpty() || editTextPhone.text.toString() != clientRegister.telefone
            ) {
                userIsValid = false
            }
            if (userIsValid && editTextPhone.text.toString() == clientRegister.telefone) {
                onSuccessRegister(client = clientRegister)
            } else {
                val state: Boolean = checkFieldsState()
                if (state) {
                    registerUser()
                }
            }
        }
        bindBUttons(imageView2)
    }

    override fun onStart() {
        super.onStart()
        val mf = SimpleMaskFormatter("NNNNNNNNN")
        val mtw = SimpleMaskTextWatcher(editTextPhone, mf)
        editTextPhone.addTextChangedListener(mtw)

        val mfCep = SimpleMaskFormatter("NNNNN-NNN")
        val mtwCep = SimpleMaskTextWatcher(editTextCep, mfCep)
        editTextCep.addTextChangedListener(mtwCep)
    }
    override fun onResume() {
        super.onResume()
        editTextPhone.text.clear()
    }

    private fun searchCep() {
        editTextCep.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                getCep(editTextCep.text.toString())
            }

        })
    }

    private fun containsUser(editTextPhone: EditText) {
        editTextPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                queryUser(s.toString().toLowerCase(Locale.ROOT))
            }
        })
    }

    private fun saveUser(client: Client) = CoroutineScope(Dispatchers.IO).launch {
        try {
            FirebaseRepository.personCollectionReference.add(client).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@ActivityRegisterUser,
                    "Dado cadastrado com sucesso!",
                    Toast.LENGTH_SHORT
                ).show()
                onSuccessRegister(client)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ActivityRegisterUser, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser() {
        val nome = editTextName.text.toString()
        val telefone = editTextPhone.text.toString()
        val cep = editTextCep.text.toString()
        val endereco = editTextTextAddress.text.toString()
        val bairro = editTextBairro.text.toString()
        val complemento = editTextComplemento.text.toString()
        val numero = editTextTextNumber.text.toString()

        val client = Client(
            nome,
            telefone,
            cep,
            endereco,
            bairro,
            complemento,
            numero
        )
        saveUser(client = client)
    }

    private fun getCep(cep: String) {
        val call = ConfigService.getInstance()?.getCep(cep = cep)
        call?.enqueue(object : Callback<Endereco> {
            override fun onResponse(call: Call<Endereco>, response: Response<Endereco>) {
                if (response.isSuccessful) {
                    editTextTextAddress.setText(response.body()?.address.toString())
                    editTextBairro.setText(response.body()?.district.toString())
                    val calculo: Double = UtilsDelivery().calcDelivery(
                        Constants.lat,
                        Constants.lng,
                        response.body()?.lat!!,
                        response.body()?.lng!!
                    )
                    frete.apply {
                        this.frete = calculo
                    }
                    val textView = findViewById<TextView>(R.id.textView39)
                    textView.text = ("Frete RS " + decimalFormat.format(calculo))
                }
            }

            override fun onFailure(call: Call<Endereco>, t: Throwable) {
                Log.d("Erro", t.message.toString())
            }

        })
    }

    private fun onSuccessRegister(client: Client) {
        val intent = Intent(applicationContext, PaymentActivity::class.java)
        intent.putExtra("cliente", client)
        intent.putExtra("frete", frete)
        startActivity(intent)
    }

    private fun queryUser(phone: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val query =
                FirebaseRepository.personCollectionReference.whereEqualTo("telefone", phone)
                    .get().await()

            query.documents.map {
                val client = it.toObject<Client>()
                if (client?.telefone.equals(phone)) {
                    withContext(Dispatchers.Main) {
                        editTextName.setText(client?.nome.toString())
                        editTextCep.setText(client?.cep.toString())
                        editTextBairro.setText(client?.bairro.toString())
                        editTextComplemento.setText(client?.complemento.toString())
                        editTextTextAddress.setText(client?.endereco.toString())
                        editTextTextNumber.setText(client?.numero.toString())
                        if (client != null) {
                            clientRegister.apply {
                                this.nome = client.nome
                                this.telefone = client.telefone
                                this.cep = client.cep
                                this.endereco = client.endereco
                                this.bairro = client.bairro
                                this.complemento = client.complemento
                                this.numero = client.numero
                            }
                        }
                        userIsValid = true
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        userIsValid = false
                        Toast.makeText(
                            this@ActivityRegisterUser,
                            "Houve um problema ao encontrar usuário, tente novamente",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                userIsValid = false
                Toast.makeText(this@ActivityRegisterUser, e.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun checkFieldsState(): Boolean {
        ValidFields.run {
            if (!validFields(editText = editTextName)) {
                MessageView.showMessage(context = this@ActivityRegisterUser, fieldName = "Nome")
                return false
            } else if (!validFields(editText = editTextComplemento)) {
                MessageView.showMessage(
                    context = this@ActivityRegisterUser,
                    fieldName = "Complemento"
                )
                return false
            } else if (!validFields(editText = editTextBairro)) {
                MessageView.showMessage(
                    context = this@ActivityRegisterUser,
                    fieldName = "Bairro"
                )
                return false

            } else if (!validFields(editText = editTextTextAddress)) {
                MessageView.showMessage(context = this@ActivityRegisterUser, fieldName = "Endereço")
                return false
            } else if (!validFields(editText = editTextTextNumber)) {
                MessageView.showMessage(context = this@ActivityRegisterUser, fieldName = "Número")
                return false
            } else if (!validFields(editText = editTextPhone)) {
                MessageView.showMessage(context = this@ActivityRegisterUser, fieldName = "Telefone")
                return false
            } else if (!validFields(editText = editTextCep) && editTextCep.text.length == 8) {
                MessageView.showMessage(context = this@ActivityRegisterUser, fieldName = "cep")
                return false
            }
            return true
        }
    }

    private fun bindBUttons(imageViewBack: ImageView) {
        imageViewBack.setOnClickListener {
            onBackPressed()
        }
    }

//    private fun calcDelivery(
//        latLocal: Double,
//        lngLocal: Double,
//        latInicial: Double,
//        lngInicial: Double
//    ) {
//        val KM: Double = 6371.0
//        val latDistance = Math.toRadians(latLocal - latInicial)
//        val lngDistance = Math.toRadians(lngLocal - lngInicial)
//        val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
//                Math.cos(Math.toRadians(latLocal)) * Math.cos(Math.toRadians(latInicial)) *
//                Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2)
//        val c: Double = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
//
//        var result: Long = (Math.round(KM * c))
//        var valorFrete: Double = result.toDouble()
//
//        if (result in 2..5) {
//            valorFrete *= 1.25
//        } else if (result > 5) {
//            valorFrete *= 1.50
//        }
//
//        val dd = ValidFields.calcDelivery(25.2,256.2,254.6,25.2)
//
//
//
//    }
}
