package com.example.e_catapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.e_catapp.R
import com.example.e_catapp.api.ApiConfig
import com.example.e_catapp.helper.PrefManager
import com.example.e_catapp.models.PetAddResponse
import com.example.e_catapp.models.Transaction
import com.example.e_catapp.models.TransactionResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookingDoneActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_complete)

        prefManager = PrefManager(this)
        val back = findViewById<Button>(R.id.back)
        val sp = intent.getStringExtra("sp")
        val sk = intent.getStringExtra("sk")
        val updetat = intent.getStringExtra("updetat")
        val konfirmasi: Button = findViewById(R.id.konfirmasi)
        val pengambilan: Button = findViewById(R.id.pengambilan)
        val condown: LinearLayout = findViewById(R.id.condown)
        val ketCondown: TextView = findViewById(R.id.ketCondown)
        val buttonPanel2: LinearLayout = findViewById(R.id.buttonPanel2)
        val pickup: TextView = findViewById(R.id.pickup)
        pickup.text = updetat
        when (sp) {
            "Diterima" -> {
                buttonPanel2.visibility = View.GONE
                ketCondown.visibility = View.GONE
            }
            "Dibatalkan" -> {
                buttonPanel2.visibility = View.GONE
                condown.visibility = View.GONE
                ketCondown.visibility = View.GONE
                pickup.visibility = View.GONE
            }
        }
        when (sk) {
            "Menunggu Konfirmasi" -> {
                pengambilan.visibility = View.GONE
            }
            "Di Konfirmasi" -> {
                konfirmasi.visibility = View.GONE
            }
            "Dibatalkan" -> {
                buttonPanel2.visibility = View.GONE
            }
        }

        setView()

        getTransaction()

        back.setOnClickListener {
            onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showTransactionData(data: Transaction){
        val bookid: TextView = findViewById(R.id.bookingID)
        val name: TextView = findViewById(R.id.name)
        val email: TextView = findViewById(R.id.email)
        val notelp: TextView = findViewById(R.id.notelp)
        val petName: TextView = findViewById(R.id.petName)
        val umur: TextView = findViewById(R.id.umur)
        val berat: TextView = findViewById(R.id.berat)
        val jenisKelamin: TextView = findViewById(R.id.jenisKelamin)
        val stavak: TextView = findViewById(R.id.stavak)
        val status: TextView = findViewById(R.id.status)
        val harga: TextView = findViewById(R.id.totalCost)
        val redDot = findViewById<Button>(R.id.redDot)
        val orenDot = findViewById<Button>(R.id.orenDot)
        val greenDot = findViewById<Button>(R.id.greenDot)
        val failPlang = findViewById<ConstraintLayout>(R.id.fail)
        val succesPlang = findViewById<ConstraintLayout>(R.id.succes)
        val pesSelesai: TextView = findViewById(R.id.pesSelesai)

        bookid.text = "BookingID: "+data.id.toString()
        name.text = data.user.name
        email.text = data.user.email
        notelp.text = data.user.no_telp
        petName.text = data.hewan.nama_hewan
        umur.text = data.hewan.umur
        berat.text = data.hewan.berat
        jenisKelamin.text = data.hewan.jenis_kelamin
        stavak.text = data.hewan.status_vaksin
        status.text = data.status
        if (data.hewan.harga == "0") harga.text = "Gratis" else harga.text = "Rp. " + data.hewan.harga
        if (data.status == "Menunggu Konfirmasi"){
            redDot.visibility = View.GONE
            greenDot.visibility = View.GONE
        }else if (data.status_penerimaan == "Diterima" && data.status == "Di Konfirmasi"){
            redDot.visibility = View.GONE
            orenDot.visibility = View.GONE
            succesPlang.visibility = View.VISIBLE
            pesSelesai.visibility = View.VISIBLE
        }else if (data.status == "Di Konfirmasi"){
            redDot.visibility = View.GONE
            orenDot.visibility = View.GONE
        }else if (data.status == "Dibatalkan"){
            greenDot.visibility = View.GONE
            orenDot.visibility = View.GONE
            failPlang.visibility = View.VISIBLE
        }

        getJenis()
    }

    private fun setView() {
        val token = prefManager.fetchAccessToken()

        val todone = intent.getStringExtra("todone")
        val bookComplete: TextView = findViewById(R.id.bookingCompleteText)
        val bookInfo: TextView = findViewById(R.id.bookingInfoText)
        val buttonPanel : LinearLayout = findViewById(R.id.buttonPanel)
        val selesai: Button = findViewById(R.id.selesai)
        val konfirmasi: Button = findViewById(R.id.konfirmasi)
        val pengambilan: Button = findViewById(R.id.pengambilan)
        val batal: Button = findViewById(R.id.batal)
        val buttonPanel2: LinearLayout = findViewById(R.id.buttonPanel2)
        val succesPlang = findViewById<ConstraintLayout>(R.id.succes)
        val pesDibuat: TextView = findViewById(R.id.pesDibuat)

        if (token != null) {
            if (prefManager.getrole() == 1) {
                bookComplete.visibility = View.INVISIBLE
                selesai.visibility = View.GONE
                val idTransaksi = intent.extras?.getInt("idTransaksiView")

                konfirmasi.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("Konfirmasi Pemesanan")
                        .setMessage("Konfirmasi pesanan ini?")
                        .setPositiveButton("Ya") { dialog, which ->
                            updateStatusKP(idTransaksi)
                            val intent = Intent(this, MainAdminActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            val fragment = "transaksi"
                            intent.putExtra("fragment", fragment)
                            startActivity(intent)
                        }
                        .setNegativeButton("Tidak") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }

                pengambilan.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("Konfirmasi Pengambilan")
                        .setMessage("Apakah anda yakin ingin meyelesaikan pemesanan ini?")
                        .setPositiveButton("Ya") { dialog, which ->
                            updateStatusAS(idTransaksi)
                            val intent = Intent(this, MainAdminActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            val fragment = "transaksi"
                            intent.putExtra("fragment", fragment)
                            startActivity(intent)
                        }
                        .setNegativeButton("Tidak") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }

                batal.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("Batalkan Pesanan")
                        .setMessage("Apakah anda yakin ingin membatalkan pemesanan ini?")
                        .setPositiveButton("Ya") { dialog, which ->
                            setBatal()
                            val intent = Intent(this, MainAdminActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            val fragment = "transaksi"
                            intent.putExtra("fragment", fragment)
                            startActivity(intent)
                        }
                        .setNegativeButton("Tidak") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }
            } else if (prefManager.getrole() == 2 && todone == "todone") {
                bookInfo.visibility = View.GONE
                buttonPanel2.visibility = View.GONE
                succesPlang.visibility = View.VISIBLE
                pesDibuat.visibility = View.VISIBLE

                selesai.setOnClickListener {
                    val intent = Intent(this, MainActivity::class.java)
                    val fragment = "history"
                    intent.putExtra("fragment", fragment)
                    startActivity(intent)
                }
            } else if (prefManager.getrole() == 2 && todone == "check") {
                bookInfo.visibility = View.GONE
                buttonPanel.visibility = View.GONE

            }
        }
    }

    private fun getJenis(){
        val jenis: TextView = findViewById(R.id.jenis)
        val api = ApiConfig.Create(this)
        val idPet = intent.extras?.getInt("idPet")
        val callData = idPet?.let { api.getJenisHewan(it) }

        callData?.enqueue(object : Callback<PetAddResponse> {
            override fun onResponse(
                call: Call<PetAddResponse>,
                response: Response<PetAddResponse>,
            ) {
                if (response.isSuccessful) {
                    val petResponse = response.body()
                    petResponse?.let {
                        val data = it.data
                        jenis.text = data.type.nama_type
                        Log.e("Data", data.toString())
                    }
                } else {
                    Log.e("Response Error", response.message())
                }
            }

            override fun onFailure(call: Call<PetAddResponse>, t: Throwable) {
                Log.e("ERROR", t.message.toString())
            }
        })
    }

    private fun getTransaction() {
        val api = ApiConfig.Create(this)
        val idTransaksi = intent.extras?.getInt("idTransaksiView")
        val callData = idTransaksi?.let { api.getTransactions(it) }

        callData?.enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(
                call: Call<TransactionResponse>,
                response: Response<TransactionResponse>,
            ) {
                if (response.isSuccessful) {
                    val transactionResponse = response.body()
                    transactionResponse?.let {
                        val data = it.data
                        showTransactionData(data)

                        Log.e("Data", data.toString())
                    }
                } else {
                    Log.e("Response Error", response.message())
                }
            }

            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                Log.e("ERROR", t.message.toString())
            }
        })
    }

    private fun setBatal() {
        val idTransaksi = intent.extras?.getInt("idTransaksiView")

        updateStatusGagal(idTransaksi)
        val intent = Intent(this, MainAdminActivity::class.java)
        startActivity(intent)
    }

    private fun updateStatusKP(idTransaksi: Int?) {
        val api = ApiConfig.Create(this)
        val status = "Di Konfirmasi"
        val status_penerimaan = "Belum Diterima"
        val callData = idTransaksi?.let { api.updateTransaksiStatus(it, status, status_penerimaan) }

        callData?.enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(call: Call<TransactionResponse>, response: Response<TransactionResponse>) {
                if (response.isSuccessful) {
                    Log.e("Status Update", "Pemesanan telah disetujui.")
                } else {
                    Log.e("Status Update Error", response.message())
                }
            }

            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                Log.e("Status Update Error", t.message.toString())
            }
        })
    }

    private fun updateStatusAS(idTransaksi: Int?) {
        val api = ApiConfig.Create(this)
        val status = "Selesai"
        val status_penerimaan = "Diterima"
        val callData = idTransaksi?.let { api.updateTransaksiStatus(it, status, status_penerimaan) }

        callData?.enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(call: Call<TransactionResponse>, response: Response<TransactionResponse>) {
                if (response.isSuccessful) {
                    Log.e("Status Update", "Hewan telah diadopsi.")
                } else {
                    Log.e("Status Update Error", response.message())
                }
            }

            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                Log.e("Status Update Error", t.message.toString())
            }
        })
    }

    private fun updateStatusGagal(idTransaksi: Int?) {
        val api = ApiConfig.Create(this)
        val status = "Dibatalkan"
        val status_penerimaan = "Dibatalkan"
        val callData = idTransaksi?.let { api.updateTransaksiStatus(it, status, status_penerimaan) }

        callData?.enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(call: Call<TransactionResponse>, response: Response<TransactionResponse>) {
                if (response.isSuccessful) {
                    Log.e("Status Update", "Pemesanan gagal.")
                } else {
                    Log.e("Status Update Error", response.message())
                }
            }

            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                Log.e("Status Update Error", t.message.toString())
            }
        })
    }
}
