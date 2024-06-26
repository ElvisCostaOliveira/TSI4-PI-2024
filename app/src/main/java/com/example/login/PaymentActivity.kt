package com.example.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nexus_app.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import com.google.gson.GsonBuilder
import retrofit2.http.Query

class PaymentActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var radioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.paymentactivity)

        val sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", 0)

        val totalValue = intent.getStringExtra("TOTAL")?.toDoubleOrNull()
        val productList = intent.getParcelableArrayListExtra<Produtos>("PRODUCT_LIST")

        findViewById<TextView>(R.id.totalValueText).text = totalValue.toString()

        val cardNumberInput: EditText = findViewById(R.id.cardNumberInput)
        val cardExpirationInput: EditText = findViewById(R.id.cardExpirationInput)
        val cardCVCInput: EditText = findViewById(R.id.cardCVCInput)
        val finishPaymentButton: Button = findViewById(R.id.finishPaymentButton)

        radioGroup = findViewById(R.id.addressRadioGroup)

        loadUserAddresses(userId)

        finishPaymentButton.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                val selectedAddressId = selectedRadioButton.tag.toString().toInt()
                enviaOrdem(userId, totalValue.toString().toDouble(), productList, selectedAddressId)
            } else {
                Toast.makeText(this, "Por favor, selecione um endereÃ§o", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun loadUserAddresses(userId: Int) {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://83a9304b-b06e-4d10-89a5-bfde9c740ff3-00-5cjvaavs23t6.worf.replit.dev/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val api = retrofit.create(OrderApiServicePyAc::class.java)
        val call = api.getUserAddresses(userId)
        call.enqueue(object : Callback<List<Endereco>> {
            override fun onResponse(call: Call<List<Endereco>>, response: Response<List<Endereco>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("API_RESPONSE", "Response: $responseBody")
                    response.body()?.let { addresses ->
                        populateAddressRadioButtons(addresses)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_ERROR", "Erro ao carregar endereÃ§os: $errorBody")
                    Toast.makeText(this@PaymentActivity, "Erro ao carregar endereÃ§os", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Endereco>>, t: Throwable) {
                Log.e("API_ERROR", "Falha na conexÃ£o", t)
                Toast.makeText(this@PaymentActivity, "Falha na conexÃ£o", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun populateAddressRadioButtons(addresses: List<Endereco>) {
        addresses.forEach { address ->
            val radioButton = RadioButton(this)
            radioButton.text = "${address.ENDERECO_LOGRADOURO}, ${address.ENDERECO_NUMERO} - ${address.ENDERECO_COMPLEMENTO}, ${address.ENDERECO_CIDADE} - ${address.ENDERECO_ESTADO}, ${address.ENDERECO_CEP}"
            radioButton.tag = address.ENDERECO_ID
            radioGroup.addView(radioButton)
        }
    }

    private fun enviaOrdem(userId: Int, total: Double, products: ArrayList<Produtos>?, addressId: Int) {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://20a5686b-0ea6-436a-8019-9a5e55c9dafb-00-bl1jzh23zffh.spock.replit.dev/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val api = retrofit.create(OrderApiServicePyAc::class.java)
        val orderRequest = OrderRequest(userId, total, products!!, addressId)
        val call = api.createOrder(orderRequest)
        call.enqueue(object : Callback<ResponseCompra> {
            override fun onResponse(call: Call<ResponseCompra>, response: Response<ResponseCompra>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@PaymentActivity, "Pedido realizado com sucesso", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@PaymentActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_ERROR", "Erro ao realizar pedido: $errorBody")
                    Toast.makeText(this@PaymentActivity, "Erro ao realizar pedido", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseCompra>, t: Throwable) {
                Log.e("API_ERROR", "Falha na conexÃ£o", t)
                Toast.makeText(this@PaymentActivity, "Falha na conexÃ£o", Toast.LENGTH_LONG).show()
            }
        })
    }

    interface OrderApiServicePyAc {
        @POST("/createOrder")
        fun createOrder(@Body orderRequest: OrderRequest): Call<ResponseCompra>

        @GET("/getUserAddresses")
        fun getUserAddresses(@Query("userId") userId: Int): Call<List<Endereco>>
    }

}

data class Endereco(
    val ENDERECO_ID: Int,
    val USUARIO_ID: Int,
    val ENDERECO_NOME: String,
    val ENDERECO_LOGRADOURO: String,
    val ENDERECO_NUMERO: String,
    val ENDERECO_COMPLEMENTO: String,
    val ENDERECO_CEP: String,
    val ENDERECO_CIDADE: String,
    val ENDERECO_ESTADO: String,
    val ENDERECO_APAGADO: Int
)

data class OrderRequest(
    val userId: Int,
    val total: Double,
    val products: ArrayList<Produtos>,
    val addressId: Int
)

data class ResponseCompra(
    val status: String,
    val code: Int,
    val message: String
)