package com.example.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import android.content.SharedPreferences
import com.example.nexus_app.R

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)

        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            blockLogin()
        }

        loginButton.setBackgroundResource(R.color.black)
    }

    private fun blockLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://a24747b0-cd8d-4235-bc65-e4dbf25fbc1d-00-3vsoz9hjzwqe2.janeway.replit.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.login(email, password)
        call.enqueue(object : Callback<List<LoginResponse>> {
            override fun onResponse(
                call: Call<List<LoginResponse>>,
                response: Response<List<LoginResponse>>
            ) {


                if (response.isSuccessful && response.body() != null) {
                    val loginResponses = response.body()!!
                    if (loginResponses.isNotEmpty()) {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)



                        val usuarioId = loginResponses.get(0).USUARIO_ID

                        // Salvando o usuarioId no SharedPreferences
                        val sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putInt("userId", usuarioId)
                        editor.apply()


                        Toast.makeText(this@LoginActivity, "Login feito com sucesso", Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                        //finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "UsuÃ¡rio ou senha invÃ¡lidos",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Log.e(
                        "com.example.login.LoginActivity",
                        "Login failed: HTTP error code: " + response.code() + " msg: " + response.message()
                    )
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_LONG).show()
                }

            }

            override fun onFailure(call: Call<List<LoginResponse>>, t: Throwable) {
                Log.e("com.example.login.LoginActivity", "onFailure: " + t.message)
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })


    }


    interface ApiService {
        @GET("login")
        fun login(
            @Query("usuario") usuario: String,
            @Query("senha") senha: String
        ): Call<List<LoginResponse>>
    }
}