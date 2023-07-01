package com.example.e_catapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.e_catapp.R
import com.example.e_catapp.api.ApiConfig
import com.example.e_catapp.helper.PrefManager
import com.example.e_catapp.models.FormResponse
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        val login: TextView = findViewById(R.id.login)
        val back: ImageView = findViewById(R.id.kembali)

        prefManager = PrefManager(this)

        checkRole()

        login.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }

        back.setOnClickListener{
            onBackPressed()
        }

    }

    private fun Daftar() {

        val etNama: TextInputLayout = findViewById(R.id.namalengkap)
        val etAlamat: TextInputLayout = findViewById(R.id.alamat)
        val etNoHp: TextInputLayout = findViewById(R.id.notelp)
        val etEmail: TextInputLayout = findViewById(R.id.email)
        val etPassword1: TextInputLayout = findViewById(R.id.password_1)
        val etPassword2: TextInputLayout = findViewById(R.id.password_2)
        val etRole: EditText =findViewById(R.id.role_id)
        val tambah: Button = findViewById(R.id.btntambah)

        tambah.setOnClickListener {
            val apiClient = ApiConfig.Create(this)
            val name = etNama.getEditText()?.getText().toString().trim()
            val alamat = etAlamat.getEditText()?.getText().toString().trim()
            val nohp = etNoHp.getEditText()?.getText().toString().trim()
            val emaill = etEmail.getEditText()?.getText().toString().trim()
            val pw = etPassword1.getEditText()?.getText().toString().trim()
            val pw2 = etPassword2.getEditText()?.getText().toString().trim()
            val role = etRole.text.toString()
            val sendData = apiClient.tambah(
                name,
                alamat,
                nohp,
                emaill,
                pw,
                role.toInt()
            )
            if (name.isEmpty()){
                etNama.setError("Nama Tidak boleh Kosong")
            }
            else if (alamat.isEmpty()){
                etAlamat.setError("Alamat Tidak boleh Kosong")
            }
            else if (nohp.isEmpty()){
                etNoHp.setError("No.Hp Tidak boleh Kosong")
            }
            else if (emaill.isEmpty()){
                etEmail.setError("Email Tidak boleh Kosong")
            }
            else if (pw.isEmpty()){
                etPassword1.setError("Password Tidak boleh Kosong")
            }
            else if (pw2 != pw){
                etPassword2.setError("Konfirmasi Password harus sama dengan Password!")
            }
            else {
                sendData.enqueue(object : Callback<FormResponse> {
                    override fun onResponse(
                        call: Call<FormResponse>,
                        response: Response<FormResponse>,
                    ) {
                        val data = response.body()
                        Log.e("ss",sendData.toString())
                        if (data?.status == true) {
                            Toast.makeText(this@RegisterActivity, data.message, Toast.LENGTH_SHORT).show()
                            onBackPressed()
                            finish()
                        } else {
                            Toast.makeText(this@RegisterActivity, "Data gagal ditambahkan", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<FormResponse>, t: Throwable) {
                        Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

    }

    private fun Register() {

        val etNama: TextInputLayout = findViewById(R.id.namalengkap)
        val etAlamat: TextInputLayout = findViewById(R.id.alamat)
        val etNoHp: TextInputLayout = findViewById(R.id.notelp)
        val etEmail: TextInputLayout = findViewById(R.id.email)
        val etPassword1: TextInputLayout = findViewById(R.id.password_1)
        val etPassword2: TextInputLayout = findViewById(R.id.password_2)
        val etRole: EditText =findViewById(R.id.role_id)

        val register: Button = findViewById(R.id.register)


        register.setOnClickListener {
            val apiClient = ApiConfig.Create(this)
            val name = etNama.getEditText()?.getText().toString().trim()
            val alamat = etAlamat.getEditText()?.getText().toString().trim()
            val nohp = etNoHp.getEditText()?.getText().toString().trim()
            val emaill = etEmail.getEditText()?.getText().toString().trim()
            val pw = etPassword1.getEditText()?.getText().toString().trim()
            val pw2 = etPassword2.getEditText()?.getText().toString().trim()
            val role = etRole.text.toString()
            val sendData = apiClient.tambah(
                name,
                alamat,
                nohp,
                emaill,
                pw,
                role.toInt()
            )
            if (name.isEmpty()){
                etNama.setError("Nama Tidak boleh Kosong")
            }
            else if (alamat.isEmpty()){
                etAlamat.setError("Alamat Tidak boleh Kosong")
            }
            else if (nohp.isEmpty()){
                etNoHp.setError("No.Hp Tidak boleh Kosong")
            }
            else if (emaill.isEmpty()){
                etEmail.setError("Email Tidak boleh Kosong")
            }
            else if (pw.isEmpty()){
                etPassword1.setError("Password Tidak boleh Kosong")
            }
            else if (pw2 != pw){
                etPassword2.setError("Konfirmasi Password harus sama dengan Password!")
            }
            else {
                sendData.enqueue(object : Callback<FormResponse> {
                    override fun onResponse(
                        call: Call<FormResponse>,
                        response: Response<FormResponse>,
                    ) {
                        val data = response.body()
                        Log.e("ss",sendData.toString())
                        if (data?.status == true) {
                            val i = Intent(this@RegisterActivity, LoginActivity::class.java)
                            Toast.makeText(this@RegisterActivity, data.message, Toast.LENGTH_SHORT).show()
                            startActivity(i)
                            finish()
                        } else {
                            Toast.makeText(this@RegisterActivity, "Data gagal ditambahkan", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<FormResponse>, t: Throwable) {
                        Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

    }

    private fun checkRole() {
        val token = prefManager.fetchAccessToken()
        val daftar: TextView = findViewById(R.id.daftar)
        val btnRegist: Button = findViewById(R.id.register)
        val tambahUser: TextView = findViewById(R.id.adduser)
        val btnTambah: Button = findViewById(R.id.btntambah)
        val kalimat : TextView = findViewById(R.id.a_member)
        val login: TextView = findViewById(R.id.login)
        val back: ImageView = findViewById(R.id.kembali)

        if (token != null) {
            if (prefManager.isLogin()!!  && prefManager.getrole() == 1 ){
                tambahUser.visibility = View.VISIBLE
                btnTambah.visibility = View.VISIBLE
                back.visibility = View.VISIBLE
                kalimat.visibility = View.INVISIBLE
                Daftar()
            }
        }
        else {
            daftar.visibility = View.VISIBLE
            btnRegist.visibility = View.VISIBLE
            kalimat.visibility = View.VISIBLE
            login.visibility = View.VISIBLE
            Register()
        }
    }
}