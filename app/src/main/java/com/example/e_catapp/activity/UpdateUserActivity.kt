package com.example.e_catapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.e_catapp.R
import com.example.e_catapp.api.ApiConfig
import com.example.e_catapp.helper.PrefManager
import com.example.e_catapp.models.FormResponse
import com.example.e_catapp.models.ProfileResponse
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateUserActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user)

        prefManager = PrefManager(this)

        checkLoginRole()

        val back: ImageView = findViewById(R.id.kembali)
        back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun updatePassword() {
        val pw1: TextInputEditText = findViewById(R.id.password_1)
        val pw2: TextInputEditText = findViewById(R.id.password_2)

        val newPassword = pw1.text.toString()
        val konfPassword = pw2.text.toString()

        val api = ApiConfig.Create(this)
        val id = intent.extras?.getInt("id")
        val call = id?.let { api.updatePassword(newPassword, konfPassword) }

        if (newPassword.isBlank()){
            Toast.makeText(this@UpdateUserActivity, "Data tidak valid!", Toast.LENGTH_SHORT).show()
            pw1.error = "Password baru tidak boleh kosong!"
        }
        else if (konfPassword.isBlank()){
            Toast.makeText(this@UpdateUserActivity, "Data tidak valid!", Toast.LENGTH_SHORT).show()
            pw2.error = "Konfirmasi Password baru tidak boleh kosong!"
        }
        else if (konfPassword != newPassword){
            Toast.makeText(this@UpdateUserActivity, "Data tidak valid!", Toast.LENGTH_SHORT).show()
            pw2.error = "Konfirmasi password = password baru!"
        }
        else if (konfPassword == newPassword){
            call?.enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                    if (response.isSuccessful) {
                        Log.d("TAG", "onResponse: ${response.body()}")
                        val profileResponse = response.body()
                        Toast.makeText(this@UpdateUserActivity, profileResponse?.message, Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    } else {
                        Log.d("TAG", "Call Value: ${call?.request()?.method} ${call?.request()?.url}")
                        Toast.makeText(this@UpdateUserActivity, "Gagal memperbarui password", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Toast.makeText(this@UpdateUserActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showProfile() {
        val api = ApiConfig.Create(this)
        val callData = api.getProfile()

        callData.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>,
            ) {
                if (response.isSuccessful) {
                    val profileresponse = response.body()
                    profileresponse?.let {
                        val data = it.data
                        val etNama: TextInputEditText = findViewById(R.id.namalengkap)
                        val etAlamat: TextInputEditText = findViewById(R.id.alamat)
                        val etNoHp: TextInputEditText = findViewById(R.id.notelp)
                        val etEmail: TextInputEditText = findViewById(R.id.email)
                        val tvnamalengkap: TextView = findViewById(R.id.tvnamalengkap)
                        val tvalamat: TextView = findViewById(R.id.tvalamat)
                        val tvnotelp: TextView = findViewById(R.id.tvnotelp)
                        val tvemail: TextView = findViewById(R.id.tvemail)

                        etNama.setText(data.name)
                        etAlamat.setText(data.alamat)
                        etEmail.setText(data.email)
                        etNoHp.setText(data.no_telp)
                        tvnamalengkap.setText(data.name)
                        tvalamat.setText(data.alamat)
                        tvnotelp.setText(data.no_telp)
                        tvemail.setText(data.email)

                        Log.e("Data", data.toString())
                    }
                } else {
                    Log.e("Response Error", response.message())
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.e("ERROR", t.message.toString())
            }
        })
    }

    private fun deleteUser() {
        val id = intent.extras?.getInt("id")
        val api = ApiConfig.Create(this)
        val callData = id?.let { api.deleteUser(it) }
        callData?.enqueue(object : Callback<FormResponse> {
            override fun onResponse(
                call: Call<FormResponse>,
                response: Response<FormResponse>
            ) {
                Log.d("TAG", "onResponse: ${response.body()}")
                val data = response.body()
                if (response.isSuccessful) {
                    Toast.makeText(this@UpdateUserActivity, data?.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@UpdateUserActivity, MainAdminActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@UpdateUserActivity, "Gagal menghapus User", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FormResponse>, t: Throwable) {
                Toast.makeText(this@UpdateUserActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUser() {
        val nama: TextInputEditText = findViewById(R.id.namalengkap)
        val alamat: TextInputEditText = findViewById(R.id.alamat)
        val no_telp: TextInputEditText = findViewById(R.id.notelp)
        val email: TextInputEditText = findViewById(R.id.email)

        val etNama = nama.text.toString()
        val etEmail = email.text.toString()
        val etAlamat = alamat.text.toString()
        val etNoHp = no_telp.text.toString()

        val id = intent.extras?.getInt("id")
        val api = ApiConfig.Create(this)
        val callData = id?.let { api.updateUser(it, etNama, etAlamat, etNoHp, etEmail) }

        if (etNama.isBlank()) {
            Toast.makeText(this@UpdateUserActivity, "Data tidak valid!", Toast.LENGTH_SHORT).show()
            nama.error = "Nama tidak boleh kosong!"
        }
        else if (etEmail.isBlank()){
            Toast.makeText(this@UpdateUserActivity, "Data tidak valid!", Toast.LENGTH_SHORT).show()
            email.error = "Email tidak boleh kosong!"
        }
        else if (etAlamat.isBlank()){
            Toast.makeText(this@UpdateUserActivity, "Data tidak valid!", Toast.LENGTH_SHORT).show()
            alamat.error = "Alamat tidak boleh kosong!"
        }
        else if (etNoHp.isBlank()){
            Toast.makeText(this@UpdateUserActivity, "Data tidak valid!", Toast.LENGTH_SHORT).show()
            no_telp.error = "No. Hp tidak boleh kosong!"
        }
        else {
            callData?.enqueue(object : Callback<FormResponse> {
                override fun onResponse(call: Call<FormResponse>, response: Response<FormResponse>) {
                    Log.d("TAG", "onResponse: ${response.body()}")
                    val data = response.body()
                    if (response.isSuccessful) {
                        Toast.makeText(this@UpdateUserActivity, data?.message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@UpdateUserActivity, MainAdminActivity::class.java).also {
                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@UpdateUserActivity, "Gagal Update User", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<FormResponse>, t: Throwable) {
                    Toast.makeText(this@UpdateUserActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }


    }

    private fun updateOnly(){
        val nama: TextInputEditText = findViewById(R.id.namalengkap)
        val alamat: TextInputEditText = findViewById(R.id.alamat)
        val no_telp: TextInputEditText = findViewById(R.id.notelp)
        val email: TextInputEditText = findViewById(R.id.email)

        val etNama = nama.text.toString()
        val etEmail = email.text.toString()
        val etAlamat = alamat.text.toString()
        val etNoHp = no_telp.text.toString()

        val id = intent.extras?.getInt("id")
        val api = ApiConfig.Create(this)
        val callData = id?.let { api.updateProfile(etNama, etAlamat, etNoHp, etEmail) }

        if (etNama.isBlank()) {
            Toast.makeText(this@UpdateUserActivity, "Data tidak valid!", Toast.LENGTH_SHORT).show()
            nama.error = "Nama tidak boleh kosong!"
        }
        else if (etEmail.isBlank()){
            Toast.makeText(this@UpdateUserActivity, "Data tidak valid!", Toast.LENGTH_SHORT).show()
            email.error = "Email tidak boleh kosong!"
        }
        else if (etAlamat.isBlank()){
            Toast.makeText(this@UpdateUserActivity, "Data tidak valid!", Toast.LENGTH_SHORT).show()
            alamat.error = "Alamat tidak boleh kosong!"
        }
        else if (etNoHp.isBlank()){
            Toast.makeText(this@UpdateUserActivity, "Data tidak valid!", Toast.LENGTH_SHORT).show()
            no_telp.error = "No. Hp tidak boleh kosong!"
        }
        else {
            callData?.enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                    Log.d("TAG", "onResponse: ${response.body()}")
                    val data = response.body()
                    if (response.isSuccessful) {
                        Toast.makeText(this@UpdateUserActivity, data?.message, Toast.LENGTH_SHORT).show()
                        if (prefManager.getrole() == 1){
                            val intent = Intent(this@UpdateUserActivity, MainAdminActivity::class.java).also {
                                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            finish()
                        }else if (prefManager.getrole() == 2){
                            val intent = Intent(this@UpdateUserActivity, MainActivity::class.java).also {
                                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this@UpdateUserActivity, "Gagal Update Profil", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    Toast.makeText(this@UpdateUserActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }


    }

    private fun detailUser() {
        val etNama: TextInputEditText = findViewById(R.id.namalengkap)
        val etAlamat: TextInputEditText = findViewById(R.id.alamat)
        val etNoHp: TextInputEditText = findViewById(R.id.notelp)
        val etEmail: TextInputEditText = findViewById(R.id.email)

        val namanya = intent.getStringExtra("name")
        val alamatnya = intent.getStringExtra("alamat")
        val noHpnya = intent.getStringExtra("no_telp")
        val emailnya = intent.getStringExtra("email")

        etNama.setText(namanya)
        etAlamat.setText(alamatnya)
        etNoHp.setText(noHpnya)
        etEmail.setText(emailnya)
    }

    private fun checkLoginRole() {
        val token = prefManager.fetchAccessToken()

        val no = intent.getStringExtra("no")
        val layedit: LinearLayout = findViewById(R.id.linearLayout1)
        val layuppas: LinearLayout = findViewById(R.id.linearLayout2)
        val pass: TextView = findViewById(R.id.pasw)
        val layview: ConstraintLayout = findViewById(R.id.layouttv)
        val teksatas: TextView = findViewById(R.id.teksatas)
        val tvUpdate: TextView = findViewById(R.id.teksupdate)
        val ivLogout: ImageView = findViewById(R.id.logout)
        val btnUpdate: Button = findViewById(R.id.update)
        val btnHapus: Button = findViewById(R.id.hapus)
        val btnUpdateOnly: Button = findViewById(R.id.updateonly)
        val user: TextView = findViewById(R.id.e_pet)
        val profil: TextView = findViewById(R.id.profile)
        val edit: TextView = findViewById(R.id.edit)

        if (token != null) {
            if ((prefManager.getrole() == 1 || prefManager.getrole() == 2) && no == "editakun"){
                layedit.visibility = View.VISIBLE
                edit.visibility = View.VISIBLE
                profil.visibility = View.VISIBLE
                btnUpdateOnly.visibility = View.VISIBLE
                showProfile()

                btnUpdateOnly.setOnClickListener {
                    updateOnly()
                }
            } else if ((prefManager.getrole() == 1 || prefManager.getrole() == 2) && no == "gantipass") {
                layuppas.visibility = View.VISIBLE
                btnUpdateOnly.visibility = View.VISIBLE
                pass.visibility = View.VISIBLE


                btnUpdateOnly.setOnClickListener {
                    updatePassword()
                }
            } else if (prefManager.getrole() == 1 && no == "edituser") {
                layedit.visibility = View.VISIBLE
                edit.visibility = View.VISIBLE
                btnUpdate.visibility = View.VISIBLE
                btnHapus.visibility = View.VISIBLE
                user.visibility = View.VISIBLE
                detailUser()

                btnUpdate.setOnClickListener {
                    updateUser()
                }

                btnHapus.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("Hapus User")
                        .setMessage("Apakah anda yakin ingin menghapus user ini?")
                        .setPositiveButton("Ya") { dialog, which ->
                            deleteUser()
                        }
                        .setNegativeButton("Tidak") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }
            } else if (prefManager.getrole() == 1 && no == "infoakun") {
                profil.visibility = View.VISIBLE
                ivLogout.visibility = View.VISIBLE
                teksatas.visibility = View.VISIBLE
                tvUpdate.visibility = View.VISIBLE
                layview.visibility = View.VISIBLE
                showProfile()

                tvUpdate.setOnClickListener {
                    layedit.visibility = View.VISIBLE
                    edit.visibility = View.VISIBLE
                    profil.visibility = View.VISIBLE
                    btnUpdateOnly.visibility = View.VISIBLE
                    profil.visibility = View.INVISIBLE
                    ivLogout.visibility = View.INVISIBLE
                    teksatas.visibility = View.INVISIBLE
                    tvUpdate.visibility = View.INVISIBLE
                    layview.visibility = View.INVISIBLE
                    showProfile()

                    btnUpdateOnly.setOnClickListener {
                        updateOnly()
                    }
                }

                ivLogout.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("Keluar")
                        .setMessage("Apakah anda yakin ingin keluar?")
                        .setPositiveButton("Ya") { dialog, which ->
                            prefManager.deleteAccessToken()

                            val loginPage = Intent(this, LoginActivity::class.java)
                            loginPage.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(loginPage)
                            Toast.makeText(this, "Mengeluarkan akun", Toast.LENGTH_SHORT).show()
                            true
                        }
                        .setNegativeButton("Tidak") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        }
    }

}