package com.example.e_catapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.e_catapp.R
import com.example.e_catapp.api.ApiConfig
import com.example.e_catapp.helper.PrefManager
import com.example.e_catapp.models.FormResponse
import com.example.e_catapp.models.JenisAddResponse
import com.example.e_catapp.models.JenisResponse
import com.example.e_catapp.models.PetAddResponse
import com.example.e_catapp.models.ProfileResponse
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class TypeActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type)

        val no = intent.getStringExtra("no")
        val back: ImageView = findViewById(R.id.kembali)
        val batal: Button = findViewById(R.id.batal)
        val tambah: Button = findViewById(R.id.tambah)
        val btnUpdate: Button = findViewById(R.id.update)
        val btnHapus: Button = findViewById(R.id.hapus)
        val edit: TextView = findViewById(R.id.editjenis)
        val nambah: TextView = findViewById(R.id.tambahjenis)

        if (no == "tambahjenis"){
            edit.visibility = View.INVISIBLE
            btnHapus.visibility = View.INVISIBLE
            btnUpdate.visibility = View.INVISIBLE

            batal.setOnClickListener {
                onBackPressed()
            }

            tambah.setOnClickListener {
                addType()
            }
        }
        else if (no == "editjenis"){
            tambah.visibility = View.INVISIBLE
            batal.visibility = View.INVISIBLE
            nambah.visibility = View.INVISIBLE

            detailType()

            btnHapus.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Hapus User")
                    .setMessage("Apakah anda yakin ingin menghapus jenis ini?")
                    .setPositiveButton("Ya") { dialog, which ->
                        deleteType()
                    }
                    .setNegativeButton("Tidak") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }

            btnUpdate.setOnClickListener {
                updateType()
            }
        }

        back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun addType() {
        val etJenis: TextInputEditText = findViewById(R.id.idJenis)

        val apiClient = ApiConfig.Create(this)
        val nama_jenis = etJenis.text.toString()
        val sendData = apiClient.addType(
            nama_jenis
        )
        sendData.enqueue(object : Callback<JenisAddResponse> {
            override fun onResponse(
                call: Call<JenisAddResponse>,
                response: Response<JenisAddResponse>
            ) {
                val respon = response.body()
                if (response.isSuccessful && respon != null) {
                    Toast.makeText(this@TypeActivity, respon.message, Toast.LENGTH_LONG).show()
                    val intent = Intent(this@TypeActivity, MainAdminActivity::class.java)
                    startActivity(intent)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (!errorBody.isNullOrEmpty()) {
                        Log.e("respon", errorBody)
                        errorBody
                    } else {
                        "Data gagal ditambahkan"
                    }
                    Log.e("respon", errorMessage)
                    Toast.makeText(
                        this@TypeActivity,
                        errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<JenisAddResponse>, t: Throwable) {
                Log.e("respon", t.toString())
                Toast.makeText(this@TypeActivity, t.message, Toast.LENGTH_SHORT).show()
                val intent = Intent(this@TypeActivity, MainAdminActivity::class.java)
                startActivity(intent)
            }
        })
    }

    private fun deleteType() {
        val id = intent.extras?.getInt("id")
        val api = ApiConfig.Create(this)
        val callData = id?.let { api.deleteJenis(it) }
        callData?.enqueue(object : Callback<JenisResponse> {
            override fun onResponse(
                call: Call<JenisResponse>,
                response: Response<JenisResponse>
            ) {
                Log.d("TAG", "onResponse: ${response.body()}")
                val data = response.body()
                if (response.isSuccessful) {
                    Toast.makeText(this@TypeActivity, data?.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@TypeActivity, MainAdminActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@TypeActivity, "Gagal menghapus User", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JenisResponse>, t: Throwable) {
                Toast.makeText(this@TypeActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateType() {
        val etJenis: TextInputEditText = findViewById(R.id.idJenis)

        val namaType = etJenis.text.toString()

        val id = intent.extras?.getInt("id")
        val api = ApiConfig.Create(this)
        val callData = id?.let { api.updateJenis(it, namaType) }

        if (namaType.isBlank()) {
            Toast.makeText(this@TypeActivity, "Data tidak valid!", Toast.LENGTH_SHORT).show()
            etJenis.error = "Nama Jenis tidak boleh kosong!"
        } else {
            callData?.enqueue(object : Callback<JenisAddResponse> {
                override fun onResponse(
                    call: Call<JenisAddResponse>,
                    response: Response<JenisAddResponse>
                ) {
                    Log.d("TAG", "onResponse: ${response.body()}")
                    val data = response.body()
                    if (response.isSuccessful && data != null) {
                        Toast.makeText(this@TypeActivity, data?.message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@TypeActivity, MainAdminActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@TypeActivity,
                            "Gagal Update Jenis",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<JenisAddResponse>, t: Throwable) {
                    Log.e("Error", t.toString())
                    Toast.makeText(this@TypeActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun detailType() {
        val etJenis: TextInputEditText = findViewById(R.id.idJenis)

        val nama_jenis = intent.getStringExtra("nama_type")

        etJenis.setText(nama_jenis)
    }
}