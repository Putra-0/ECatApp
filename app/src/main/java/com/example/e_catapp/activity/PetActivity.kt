package com.example.e_catapp.activity

import JenisAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
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
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.e_catapp.R
import com.example.e_catapp.adapter.ImageAddAdapter
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
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PetActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var imageAddAdapter: ImageAddAdapter
    private val selectedImageUris: MutableList<Uri> = mutableListOf()
    private lateinit var spinner: Spinner
    private lateinit var selectedJenis: Jenis

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet)

        val no = intent.getStringExtra("no")
        val back: ImageView = findViewById(R.id.kembali)
        val buttonSelectImage: Button = findViewById(R.id.load)
        val batal: Button = findViewById(R.id.batal)
        val tambah: Button = findViewById(R.id.tambah)
        val btnUpdate: Button = findViewById(R.id.update)
        val btnHapus: Button = findViewById(R.id.hapus)
        val edit: TextView = findViewById(R.id.edithewan)
        val addnew: TextView = findViewById(R.id.tambahhewan)
        val indicator: CircleIndicator3 = findViewById(R.id.indikator)

        viewPager = findViewById(R.id.viewPet)

        spinner = findViewById(R.id.jenis)

        imageAddAdapter = ImageAddAdapter(this, selectedImageUris)
        viewPager.adapter = imageAddAdapter

        buttonSelectImage.setOnClickListener {
            openImagePicker()
        }

        // Ambil data jenis dari database menggunakan Retrofit
        showSpinner()

        setupImageDelete()

        if (no == "tambahhewan"){
            edit.visibility = View.INVISIBLE
            btnHapus.visibility = View.GONE
            btnUpdate.visibility = View.GONE

            batal.setOnClickListener {
                onBackPressed()
            }

            tambah.setOnClickListener {
                addPet(selectedImageUris)
            }
        }
        else if (no == "edithewan"){
            addnew.visibility = View.INVISIBLE
            tambah.visibility = View.INVISIBLE
            batal.visibility = View.INVISIBLE

            detailPet()

            btnHapus.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Hapus User")
                    .setMessage("Apakah anda yakin ingin menghapus user ini?")
                    .setPositiveButton("Ya") { dialog, which ->
                        deletePet()
                    }
                    .setNegativeButton("Tidak") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }

            btnUpdate.setOnClickListener {
                updatePet(selectedImageUris)
            }
        }

        back.setOnClickListener{
            onBackPressed()
        }
        indicator.setViewPager(viewPager)

    }

    private fun detailPet(){
        val etNama: TextInputEditText = findViewById(R.id.idNama)
        val etUmur: TextInputEditText = findViewById(R.id.idUmur)
        val etBerat: TextInputEditText = findViewById(R.id.idBerat)
        val etHarga: TextInputEditText = findViewById(R.id.idHarga)
        val etDeskripsi: TextInputEditText = findViewById(R.id.idDeskripsi)
        val rbJantan: RadioButton = findViewById(R.id.j)
        val rbBetina: RadioButton = findViewById(R.id.b)
        val rbSudah: RadioButton = findViewById(R.id.sudah)
        val rbBelum: RadioButton = findViewById(R.id.belum)

        val nama = intent.getStringExtra("nama_hewan")
        val deskripsi = intent.getStringExtra("description")
        val jenisKelamin = intent.getStringExtra("jenis_kelamin")
        val berat = intent.getStringExtra("berat")
        val harga = intent.getStringExtra("harga")
        val umur = intent.getStringExtra("umur")
        val status_vaksin = intent.getStringExtra("status_vaksin")
        val imageUrls = intent.getStringArrayListExtra("images")

// Mengonversi daftar URL menjadi URI dan menambahkannya ke dalam MutableList
        if (imageUrls != null) {
            for (imageUrl in imageUrls) {
                val uri = Uri.parse(imageUrl)
                selectedImageUris.add(uri)
            }
        }

        etNama.setText(nama)
        etDeskripsi.setText(deskripsi)
        etBerat.setText(berat)
        etHarga.setText(harga)
        etUmur.setText(umur)
        if (jenisKelamin == "Jantan") {
            rbJantan.isChecked = true
        } else if (jenisKelamin == "Betina") {
            rbBetina.isChecked = true
        }
        if (status_vaksin == "Sudah") {
            rbSudah.isChecked = true
        } else if (status_vaksin == "Belum") {
            rbBelum.isChecked = true
        }
    }

    private fun updatePet(images: List<Uri>) {
        val etNama: TextInputLayout = findViewById(R.id.namahewan)
        val etUmur: TextInputLayout = findViewById(R.id.usia)
        val etBerat: TextInputLayout = findViewById(R.id.berat)
        val etHarga: TextInputLayout = findViewById(R.id.harga)
        val etDeskripsi: TextInputLayout = findViewById(R.id.deskripsi)
        val rgJk: RadioGroup = findViewById(R.id.rb_jk)
        val rbJk: RadioButton = findViewById(rgJk.checkedRadioButtonId)
        val rgSv: RadioGroup = findViewById(R.id.rb_vaksin)
        val rbSv: RadioButton = findViewById(rgSv.checkedRadioButtonId)

        val nama_hewan = etNama.editText?.text.toString().trim()
        val jenis_kelamin = rbJk.text.toString()
        val selectedId = selectedJenis.id
        val status_vaksin = rbSv.text.toString()
        val description = etDeskripsi.editText?.text.toString().trim()
        val umur = etUmur.editText?.text.toString().trim()
        val berat = etBerat.editText?.text.toString().trim()
        val hargaInput = etHarga.editText?.text.toString().trim()
        val harga = if (hargaInput.isBlank()) "0" else hargaInput
        val _method = "PUT"
        val imagesParts = mutableListOf<MultipartBody.Part>()

        images.forEachIndexed { index, uri ->
            val imageUrl = uri.toString()
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(imageUrl)
            val targetFile = File(cacheDir, "image_$index.$fileExtension")

            Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        try {
                            val outputStream = FileOutputStream(targetFile)
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                            outputStream.close()

                            val requestFile = targetFile.asRequestBody("image/*".toMediaTypeOrNull())
                            val imagePart = MultipartBody.Part.createFormData("images[$index]", targetFile.name, requestFile)
                            imagesParts.add(imagePart)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
        }

        val id = intent.extras?.getInt("id")
        val apiClient = ApiConfig.Create(this)
        val updateData = id?.let {
            apiClient.updateHewan(
                it,
                nama_hewan.toRequestBody(),
                description.toRequestBody(),
                umur.toRequestBody(),
                berat.toRequestBody(),
                harga.toRequestBody(),
                status_vaksin.toRequestBody(),
                selectedId.toString().toRequestBody(),
                jenis_kelamin.toRequestBody(),
                imagesParts.toTypedArray(),
                _method.toRequestBody()
            )
        }

        updateData?.enqueue(object : Callback<PetAddResponse> {
            override fun onResponse(call: Call<PetAddResponse>, response: Response<PetAddResponse>) {
                val respon = response.body()
                if (response.isSuccessful && respon != null) {
                    Toast.makeText(this@PetActivity, respon.message, Toast.LENGTH_LONG).show()
                    val intent = Intent(this@PetActivity, MainAdminActivity::class.java)
                    startActivity(intent)
                }
//                else {
//                    val errorBody = response.errorBody()?.string()
//                    val errorMessage = if (!errorBody.isNullOrEmpty()) {
//                        Log.e("respon", errorBody)
//                        errorBody
//                    } else {
//                        "Gagal memperbarui data hewan"
//                    }
//                    Log.e("respon", errorMessage)
//                }
            }

            override fun onFailure(call: Call<PetAddResponse>, t: Throwable) {
                Log.e("respon", t.toString())
                Toast.makeText(this@PetActivity, t.message, Toast.LENGTH_SHORT).show()
                val intent = Intent(this@PetActivity, MainAdminActivity::class.java)
                val fragment = "pet"
                intent.putExtra("fragment", fragment)
                startActivity(intent)
            }
        })
    }

    private fun setupImageDelete() {
        imageAddAdapter.setOnImageClickListener(object : ImageAddAdapter.OnImageClickListener {
            override fun onImageClick(position: Int) {
                showDeleteConfirmationDialog(position)
            }
        })
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Hapus Gambar")
            .setMessage("Apakah Anda yakin ingin menghapus gambar ini?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteImage(position)
            }
            .setNegativeButton("Batal", null)
            .create()
        alertDialog.show()
    }

    private fun deleteImage(position: Int) {
        selectedImageUris.removeAt(position)
        imageAddAdapter.notifyDataSetChanged()
    }

    private fun addPet(images: List<Uri>) {
        val etNama: TextInputLayout = findViewById(R.id.namahewan)
        val etUmur: TextInputLayout = findViewById(R.id.usia)
        val etBerat: TextInputLayout = findViewById(R.id.berat)
        val etHarga: TextInputLayout = findViewById(R.id.harga)
        val etDeskripsi: TextInputLayout =findViewById(R.id.deskripsi)
        val tambah: Button = findViewById(R.id.tambah)
        val rgJk: RadioGroup = findViewById(R.id.rb_jk)
        val rbJk: RadioButton = findViewById(rgJk.checkedRadioButtonId)
        val rgSv: RadioGroup = findViewById(R.id.rb_vaksin)
        val rbSv: RadioButton = findViewById(rgSv.checkedRadioButtonId)

        tambah.setOnClickListener {
            val apiClient = ApiConfig.Create(this)
            val nama_hewan = etNama.editText?.text.toString().trim()
            val jenis_kelamin = rbJk.text.toString()
            val selectedId = selectedJenis.id
            val status_vaksin = rbSv.text.toString()
            val description = etDeskripsi.editText?.text.toString().trim()
            val umur = etUmur.editText?.text.toString().trim()
            val berat = etBerat.editText?.text.toString().trim()
            val hargaInput = etHarga.editText?.text.toString().trim()
            val harga= if (hargaInput.isBlank()) "0" else hargaInput
            val imagesParts = selectedImageUris.mapIndexedNotNull { index, uri ->
                val inputStream = contentResolver.openInputStream(uri)
                inputStream?.let {
                    val file = File(cacheDir, "image_$index.jpg")
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("images[$index]", file.name, requestFile)
                }
            }

            val sendData = apiClient.addHewan(
                nama_hewan.toRequestBody(),
                description.toRequestBody(),
                umur.toRequestBody(),
                berat.toRequestBody(),
                harga.toRequestBody(),
                status_vaksin.toRequestBody(),
                selectedId.toString().toRequestBody(),
                jenis_kelamin.toRequestBody(),
                imagesParts.toTypedArray()
            )

            sendData.enqueue(object : Callback<PetAddResponse> {
                override fun onResponse(
                    call: Call<PetAddResponse>,
                    response: Response<PetAddResponse>
                ) {
                    val respon = response.body()
                    if (response.isSuccessful && respon != null) {
                        Toast.makeText(this@PetActivity, respon.message, Toast.LENGTH_LONG).show()
                        val intent = Intent(this@PetActivity, MainAdminActivity::class.java)
                        val fragment = "pet"
                        intent.putExtra("fragment", fragment)
                        startActivity(intent)
                    }
//                    else {
//                        val errorBody = response.errorBody()?.string()
//                        val errorMessage = if (!errorBody.isNullOrEmpty()) {
//                            Log.e("respon", errorBody)
//                            errorBody
//                        } else {
//                            "Data gagal ditambahkan"
//                        }
//                        Log.e("respon", errorMessage)
//                        Toast.makeText(
//                            this@PetActivity,
//                            errorMessage,
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
                }

                override fun onFailure(call: Call<PetAddResponse>, t: Throwable) {
                    Log.e("respon", t.toString())
                    Toast.makeText(this@PetActivity, t.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@PetActivity, MainAdminActivity::class.java)
                    val fragment = "pet"
                    intent.putExtra("fragment", fragment)
                    startActivity(intent)
                }
            })
        }
    }

    private fun deletePet(){
        val id = intent.extras?.getInt("id")
        val api = ApiConfig.Create(this)
        val callData = id?.let { api.deleteHewan(it) }
        callData?.enqueue(object : Callback<PetResponse> {
            override fun onResponse(
                call: Call<PetResponse>,
                response: Response<PetResponse>
            ) {
                Log.d("TAG", "onResponse: ${response.body()}")
                val data = response.body()
                if (response.isSuccessful) {
                    Toast.makeText(this@PetActivity, "Hewan terhapus", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@PetActivity, MainAdminActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    val fragment = "pet"
                    intent.putExtra("fragment", fragment)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@PetActivity, "Gagal menghapus Hewan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PetResponse>, t: Throwable) {
                Toast.makeText(this@PetActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showSpinner() {

        val apiService = ApiConfig.Create(this)
        val call = apiService.getJenis()
        val idJenis = intent.getIntExtra("id_type", -1)

        call.enqueue(object : Callback<JenisResponse> {
            override fun onResponse(
                call: Call<JenisResponse>,
                response: Response<JenisResponse>
            ) {
                if (response.isSuccessful) {
                    val jenisResponse = response.body()
                    if (jenisResponse != null && jenisResponse.status) {
                        val jenisList = jenisResponse.data
                        val adapter = JenisAdapter(this@PetActivity, jenisList)
                        spinner.adapter = adapter
                        val posisi = adapter.getPositionById(idJenis)
                        spinner.setSelection(posisi)
                        spinner.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    selectedJenis = jenisList[position]
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    // Do nothing
                                }
                            }
                    }
                }
            }

            override fun onFailure(call: Call<JenisResponse>, t: Throwable) {
                Toast.makeText(this@PetActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Pilih gambar"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                // Multiple images selected
                val clipData = data.clipData
                for (i in 0 until clipData!!.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    selectedImageUris.add(imageUri)
                }
            } else if (data?.data != null) {
                // Single image selected
                val imageUri = data.data
                if (imageUri != null) {
                    selectedImageUris.add(imageUri)
                }
            }
            imageAddAdapter.notifyDataSetChanged()
        }
    }

    private companion object {
        private const val PICK_IMAGE_REQUEST = 5
    }
}
