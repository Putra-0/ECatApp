package com.example.e_catapp.activity

import JenisAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.e_catapp.R
import com.example.e_catapp.adapter.ImageAdapter
import com.example.e_catapp.adapter.ImageAddAdapter
import com.example.e_catapp.adapter.ImageShowAdapter
import com.example.e_catapp.api.ApiConfig
import com.example.e_catapp.models.Jenis
import com.example.e_catapp.models.JenisResponse
import com.example.e_catapp.models.PetAddResponse
import com.example.e_catapp.models.PetResponse
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import me.relex.circleindicator.CircleIndicator3
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.net.URL

class PetInfoActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var imageAdapter: ImageShowAdapter
    private val selectedImageUris: MutableList<Uri> = mutableListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_info)
        val back: Button = findViewById(R.id.back)
        val btnPesan: Button = findViewById(R.id.pesan)
        val btnEdit: Button = findViewById(R.id.edit)
        val tersedia: ConstraintLayout = findViewById(R.id.available)
        val terpesan: ConstraintLayout = findViewById(R.id.booked)
        val teradopsi: ConstraintLayout = findViewById(R.id.notAvailable)
        val Nama: TextView = findViewById(R.id.title)
        val Umur: TextView = findViewById(R.id.umur)
        val Berat: TextView = findViewById(R.id.berat)
        val Harga: TextView = findViewById(R.id.harga)
        val Deskripsi: TextView = findViewById(R.id.description)
        val JenisKelamin: TextView = findViewById(R.id.jeniskelamin)
        val Jenis: TextView = findViewById(R.id.jenis)
        val StatusVaksin: TextView = findViewById(R.id.statusVak)

        val indicator: CircleIndicator3 = findViewById(R.id.indikator)

        viewPager = findViewById(R.id.viewPet)

        imageAdapter = ImageShowAdapter(this, selectedImageUris)
        viewPager.adapter = imageAdapter


        val status = intent.getStringExtra("status")
        val no = intent.getStringExtra("no")
        val id = intent.extras?.getInt("id")
        val nama = intent.getStringExtra("nama_hewan")
        val deskripsi = intent.getStringExtra("description")
        val jenisKelamin = intent.getStringExtra("jenis_kelamin")
        val berat = intent.getStringExtra("berat")
        val intentharga = intent.getStringExtra("harga")
        val umur = intent.getStringExtra("umur")
        val status_vaksin = intent.getStringExtra("status_vaksin")
        val jenis = intent.getStringExtra("nama_type")
        val imageUrls = intent.getStringArrayListExtra("images")
        val harga = if (intentharga == "0") "Gratis!" else "Rp."+intentharga

        // Membuat MutableList untuk menyimpan URI gambar

        // Mengonversi daftar URL menjadi URI dan menambahkannya ke dalam MutableList
        if (imageUrls != null) {
            for (imageUrl in imageUrls) {
                val uri = Uri.parse(imageUrl)
                selectedImageUris.add(uri)
            }
        }

        Nama.setText("Detail "+nama)
        Deskripsi.setText(deskripsi)
        JenisKelamin.setText(jenisKelamin)
        Berat.setText(berat)
        Harga.setText(harga)
        Umur.setText(umur)
        StatusVaksin.setText(status_vaksin)
        Jenis.setText(jenis)

        if (status == "Tersedia"){
            terpesan.visibility = View.GONE
            teradopsi.visibility = View.GONE

            btnPesan.setOnClickListener {
                val intent = Intent(this, BookingActivity::class.java)
                intent.putExtra("idpet", id)
                startActivity(intent)
            }
        }
        else if (status == "Sudah Dibooking"){
            tersedia.visibility = View.GONE
            teradopsi.visibility = View.GONE
            btnPesan.visibility = View.GONE
        }
        else if (status == "Tidak Tersedia"){
            tersedia.visibility = View.GONE
            terpesan.visibility = View.GONE
            btnPesan.visibility = View.GONE
        }

        if (no == "PreviewAdmin"){
            btnPesan.visibility = View.GONE

            btnEdit.setOnClickListener {
                val intent = Intent(this, PetActivity::class.java)
                val no = "edithewan"
                intent.putExtra("id", id)
                intent.putExtra("nama_hewan", nama)
                intent.putExtra("description", deskripsi)
                intent.putExtra("jenis_kelamin", jenisKelamin)
                intent.putExtra("berat", berat)
                intent.putExtra("harga", intentharga)
                intent.putExtra("umur", umur)
                intent.putExtra("status", status)
                intent.putExtra("type_id", jenis)
                intent.putExtra("status_vaksin", status_vaksin)
                intent.putStringArrayListExtra("images", ArrayList(imageUrls))
                intent.putExtra("no", no)
                startActivity(intent)
            }
        }
        else if (no == "PreviewUser"){
            btnEdit.visibility = View.GONE
        }

        back.setOnClickListener{
            onBackPressed()
        }
        indicator.setViewPager(viewPager)

    }
}
