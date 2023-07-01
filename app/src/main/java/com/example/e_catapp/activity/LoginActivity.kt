package com.example.e_catapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.e_catapp.R
import com.example.e_catapp.api.ApiConfig
import com.example.e_catapp.helper.PrefManager
import com.example.e_catapp.models.LoginResponse
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        prefManager = PrefManager(this)
        checkLogin()
        Auth()

        val register: TextView = findViewById(R.id.register)
        val forgotPassword: TextView = findViewById(R.id.forgot_password)

        register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun Auth() {
        prefManager = PrefManager(this)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val etEmail = findViewById<TextInputLayout>(R.id.email)
        val etPassword = findViewById<TextInputLayout>(R.id.password)

        btnLogin.setOnClickListener{
            val email = etEmail.getEditText()?.getText().toString().trim()
            val password = etPassword.getEditText()?.getText().toString().trim()

            if (email.isEmpty() || password.isEmpty()){
                etEmail.error = "Email not empty"
                etPassword.error = "Password not empty"
                return@setOnClickListener
            }

            val api = ApiConfig.Create(this)
            api.login(email,password).enqueue((object: Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>,
                ) {
                    val respon = response.body()
                    if (respon?.user?.role_id == 1){
                        Toast.makeText(this@LoginActivity, respon.message, Toast.LENGTH_LONG).show()
                        prefManager.saveAccessToken(respon.token)
                        prefManager.setLoggin(true)
                        val userID = respon.user.id // Ambil ID pengguna dari respons server
                        prefManager.setId(userID)
                        prefManager.setrole(1)
                        Intent(this@LoginActivity, MainAdminActivity::class.java).also {
                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(it)
                            finish()
                        }
                    } else if (respon?.user?.role_id == 2){
                        Toast.makeText(this@LoginActivity, respon.message, Toast.LENGTH_LONG).show()
                        prefManager.saveAccessToken(respon.token)
                        prefManager.setLoggin(true)
                        val userID = respon.user.id // Ambil ID pengguna dari respons server
                        prefManager.setId(userID)
                        prefManager.setrole(2)
                        Intent(this@LoginActivity, MainActivity::class.java).also {
                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(it)
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Email or Password is wrong",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, t.localizedMessage, Toast.LENGTH_LONG).show()
                }

            }))
        }
    }

    private fun checkLogin() {
        val token = prefManager.fetchAccessToken()

        if (token != null) {
            if (prefManager.isLogin()!!  && prefManager.getrole() == 1 ){
                Intent(this, MainAdminActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                    finish()
                }
            } else if (prefManager.isLogin()!! && prefManager.getrole() == 2) {
                Intent(this, MainActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                    finish()
                }
            }
        }
    }
}