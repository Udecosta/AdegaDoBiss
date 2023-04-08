package com.example.adegadobiss.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.adegadobiss.R
import com.example.adegadobiss.constants.Constants
import com.example.adegadobiss.constants.Database
import com.example.adegadobiss.constants.UtilsDelivery
import com.example.adegadobiss.model.*
import com.example.adegadobiss.network.ConfigService
import com.example.adegadobiss.network.FirebaseRepository
import com.example.adegadobiss.network.FirebaseRepository.crashlytics
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.activity_register_user.*
import kotlinx.android.synthetic.main.alert_dialog_address.*
import kotlinx.android.synthetic.main.alert_dialog_delivery.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList


class PaymentActivity : AppCompatActivity() {
    lateinit var alertDialog: AlertDialog
    private var numberOrder = 0
    private val decimalFormat = DecimalFormat("0.00")


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        crashlytics.recordException(Throwable())
        crashlytics.log("Init")
        crashlytics.setCrashlyticsCollectionEnabled(true)
        crashlytics.sendUnsentReports()

        val client = intent.getParcelableExtra<Client>("cliente")
        val frete = intent.getParcelableExtra<Frete>("frete")

        getClient(
            textView19,
            textView20,
            textView22,
            textView23,
            textView24,
            textView25,
            textView26,
            client = client
        )
        if (client != null) {
            if (frete != null) {
                showAlert(textViewSetData, client, buttonConfirmation, "FormaPagamento", frete, editextObservation.text.toString())
            }
        }

        if (client != null) {
            if (frete != null) {
                setPayment(client, arrayListOf(), frete = frete, editextObservation.text.toString())
            }
        }

        if (client != null) {
            if (frete != null) {
                getPayments(client, frete, editextObservation.text.toString())
            }
        }
        getNumberorder()

        textView21.text =
            ("Valor Total R$ " + decimalFormat.format(Database.dbProducts[Database.dbProducts.lastIndex].total))

        onBackActivity(imageView11)
    }

    private fun getNumberorder() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val myOrders = FirebaseRepository.orderCollectionReference.get().await()
            numberOrder = myOrders.documents.size
            withContext(Dispatchers.Main) {
                numberOrder += 1
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@PaymentActivity, e.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun getPayments(client: Client, frete: Frete, editTextObservation: String) = CoroutineScope(Dispatchers.IO).launch {
        val arrayList: ArrayList<Payment> = arrayListOf()
        arrayList.add(0, Payment("Selecione"))
        try {
            val getPayment = FirebaseRepository.CardsCollectionReference.get().await()
            val objects = getPayment.toObjects<Payment>()
            objects.map {
                arrayList.add(it)
            }
            withContext(Dispatchers.Main) {
                setPayment(client, arrayList, frete = frete, editTextObservation)
            }

        } catch (e: Exception) {
            Log.d("Error", e.message.toString())
        }
    }

    private fun setPayment(client: Client, arrayList: ArrayList<Payment>, frete: Frete, editTextObservation: String) {

        val spinner = findViewById<Spinner>(R.id.spinner)
        val adapter =
            ArrayAdapter<String>(this, R.layout.style_text_spinner, arrayList.map { it.tipo })
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sendMessage(
                    client,
                    buttonConfirmation,
                    parent?.selectedItem.toString(),
                    frete = frete,
                    editTextObservation
                )
                showAlert(
                    textViewSetData,
                    client,
                    buttonConfirmation,
                    parent?.selectedItem.toString(),
                    frete,
                    editTextObservation
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showAlert(
        textViewSetData: TextView,
        client: Client,
        button: Button,
        formaPagamento: String,
        frete: Frete,
        editTextObservation: String
    ) {
        textViewSetData.setOnClickListener {

            val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            val view = layoutInflater.inflate(R.layout.alert_dialog_address, null)
            val buttonConfirmAdress = view.findViewById<Button>(R.id.buttonConfirmAdress)
            val buttonCancell = view.findViewById<Button>(R.id.button3)
            val cep =
                view.findViewById<EditText>(R.id.cep).addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        client.apply {
                            this.cep = s.toString()
                        }
                        getCep(s.toString(), frete)
                    }

                })
            val endereco = view.findViewById<EditText>(R.id.editTextTextPersonName)
            val bairro = view.findViewById<EditText>(R.id.editTextTextPersonName2)
            val complemento = view.findViewById<EditText>(R.id.editTextTextPersonName3)
            val numero = view.findViewById<EditText>(R.id.editTextTextPersonName4)


            buttonConfirmAdress.setOnClickListener {
                if (endereco.text.toString().isEmpty() || bairro.text.toString()
                        .isEmpty() || complemento.text.toString()
                        .isEmpty() || numero.text.toString().isEmpty()
                ) {
                    Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                } else {
                    client.apply {
                        this.endereco = endereco.text.toString()
                        this.bairro = bairro.text.toString()
                        this.complemento = complemento.text.toString()
                        this.numero = numero.text.toString()
                    }
                    sendMessage(client, button, formaPagamento, frete = frete, editTextObservation)
                    alertDialog.dismiss()
                    textView21.text =
                        ("Valor Total R$ " + decimalFormat.format(Database.dbProducts[Database.dbProducts.lastIndex].total))
                }
            }
            buttonCancell.setOnClickListener {
                alertDialog.dismiss()
            }

            builder.setView(view)
            alertDialog = builder.create()
            alertDialog.show()
        }
    }

    private fun getClient(
        textView19: TextView,
        textView20: TextView,
        textView22: TextView,
        textView23: TextView,
        textView24: TextView,
        textView25: TextView,
        textView26: TextView,
        client: Client?
    ) {
        client.run {
            textView19.text = ("Cliente: ${client?.nome.toString()}")
            textView20.text = ("Telefone: ${client?.telefone.toString()}")
            textView22.text = ("CEP: ${client?.cep.toString()}")
            textView23.text = ("Endereço: ${client?.endereco.toString()}")
            textView24.text = ("Bairro: ${client?.bairro.toString()}")
            textView25.text = ("Complemento: ${client?.complemento.toString()}")
            textView26.text = ("Número: ${client?.numero.toString()}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendMessage(client: Client?, button: Button, formaPagamento: String, frete: Frete, editTextObservation: String) {

        val sb = StringBuilder()
        client?.run {
            sb.append(
                "Cliente: ", client.nome + "\n",
                "Telefone: ", client.telefone + "\n",
                "CEP: ", client.cep + "\n",
                "Endereço: ", client.endereco + "\n",
                "Bairro: ", client.bairro + "\n",
                "Complemento: ", client.complemento + "\n",
                "Número: ", client.numero + "\n" + "\n",
            )
        }
        sb.append("Frete RS ${decimalFormat.format(frete.frete)}\n")
        sb.append("Valor Total R$ " + decimalFormat.format(Database.dbProducts[Database.dbProducts.lastIndex].total) + "\n")
        sb.append("Número do Pedido: $numberOrder\n")
        sb.append("Forma de Pagamento: $formaPagamento \n\n")
        sb.append("Observações: $editTextObservation \n\n")

        Database.dbProducts.forEach {
            sb.append(
                "Produto: " + it.produto + "\n",
                "Quantidade: " + it.quantidade + "\n",
                "Valor dos Produtos R$ " + decimalFormat.format(it.subtotal) + "\n" + "\n",
            )
        }
        textView23.text = ("Endereço: ${client?.endereco.toString()}")
        textView24.text = ("Bairro: ${client?.bairro.toString()}")
        textView25.text = ("Complemento: ${client?.complemento.toString()}")
        textView26.text = ("Número: ${client?.numero.toString()}")
        sendOrder(sb.toString(), buttonConfirmation, client, formaPagamento, frete)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendOrder(
        order: String,
        buttonConfirm: Button,
        client: Client?,
        formaPagamento: String,
        frete: Frete
    ) {
        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val formated = currentDate.format(formatter)
        val orders: Orders = Orders(
            client?.nome.toString(),
            client?.telefone.toString(),
            client?.cep.toString(),
            client?.endereco.toString(),
            client?.bairro.toString(),
            client?.complemento.toString(),
            client?.numero.toString(),
            Database.dbProducts,
            numberOrder,
            formated.toString(),
            formaPagamento.toString(),
            Database.dbProducts[Database.dbProducts.lastIndex].total,
            frete.frete
        )
        buttonConfirm.setOnClickListener {
            if (formaPagamento != "Selecione") {
                val javaMailAPI =
                    JavaMailAPI(this, "lucasbissadega@gmail.com", "Novo Pedido", order)
                javaMailAPI.execute()

                sendOrderToFirebase(orders)


                val countDownTimer = object : CountDownTimer(5000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                    }

                    override fun onFinish() {
                        showOrderDialog(orders)
                    }
                }
                countDownTimer.start()
            } else {
                Toast.makeText(this, "Informe a forma de Pagamento", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendOrderToFirebase(orders: Orders) = CoroutineScope(Dispatchers.IO).launch {
        try {
            FirebaseRepository.orderCollectionReference.add(orders).await()
            withContext(Dispatchers.Main) {
                // alert Dialog
                Log.d("Success", "")
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.d("Error", e.message.toString())
            }
        }
    }

    private fun navigateNextScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun onBackActivity(imageView: ImageView) {
        imageView.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showOrderDialog(orders: Orders) {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val view = layoutInflater.inflate(R.layout.alert_dialog_orders, null)
        val numberOrder = view.findViewById<TextView>(R.id.textView36)
        val valueOrder = view.findViewById<TextView>(R.id.value)
        val buttonConfirm = view.findViewById<Button>(R.id.button7)
        numberOrder.text = ("Número do Pedido: " + orders.numPedido.toString().toInt())
        valueOrder.text =
            ("Valor Total R$ " + decimalFormat.format(orders.valorTotal.toString().toDouble()))
        buttonConfirm.setOnClickListener {
            navigateNextScreen()
            alertDialog.dismiss()
        }
        builder.setView(view)
        alertDialog = builder.create()
        alertDialog.show()

        alertDialog.setOnCancelListener {
            navigateNextScreen()
        }
    }

    private fun getCep(cep: String, frete: Frete) {
        val call = ConfigService.getInstance()?.getCep(cep = cep)
        call?.enqueue(object : Callback<Endereco> {
            override fun onResponse(call: Call<Endereco>, response: Response<Endereco>) {
                if (response.isSuccessful) {
                    val calculo: Double = UtilsDelivery().calcDelivery(
                        Constants.lat,
                        Constants.lng,
                        response.body()?.lat!!,
                        response.body()?.lng!!
                    )
                    frete.apply {
                        this.frete = calculo
                    }
                    var total: Double = 0.00
                    for (i in 0 until Database.dbProducts.size) {
                        total += Database.dbProducts[i].subtotal + frete.frete
                        Database.dbProducts[i].total = total
                    }
                    Toast.makeText(
                        this@PaymentActivity,
                        "Valor do frete alterado para R$ ${decimalFormat.format(calculo)}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Endereco>, t: Throwable) {
                Log.d("Erro", t.message.toString())
            }

        })
    }

}