package com.example.e_catapp.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.VolleyLog.TAG
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.e_catapp.R
import com.example.e_catapp.api.ApiConfig
import com.example.e_catapp.helper.PrefManager
import com.example.e_catapp.models.TransactionResponse
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookingActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var prefManager: PrefManager
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_pet)

        val btnPesan = findViewById<Button>(R.id.continueBooking)
        val back = findViewById<Button>(R.id.back)
        prefManager = PrefManager(this)
        textView = findViewById(R.id.pickupDate)
        setDateTime()

        val idPet = intent.extras?.getInt("idpet")

        btnPesan.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Buat Pesanan")
                .setMessage("Apakah anda yakin ingin melakukan pesanan ini?")
                .setPositiveButton("Ya") { dialog, which ->
                    createTransaction(idPet)
//                    val roleId = prefManager.getrole()
//                    val userId = prefManager.getUserId()
//
//                    if (roleId == 2 && userId != null) {
//                        val notificationTitle = "Pesanan Baru"
//                        val notificationMessage = "Pesanan baru dari user $userId"
//
//                        // Mengirim notifikasi menggunakan FCM
//                        sendNotification(notificationTitle, notificationMessage)
//                    }
                }
                .setNegativeButton("Tidak") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
        back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun createTransaction(idPet: Int?) {
        val userID = prefManager.getUserId() ?:0
        val tanggalPengambilan = getCurrentDateTime()

        if (idPet != null) {
            val api = ApiConfig.Create(this)
            val callData = api.createTransaction(userID, idPet, tanggalPengambilan)

            callData.enqueue(object : Callback<TransactionResponse> {
                override fun onResponse(
                    call: Call<TransactionResponse>,
                    response: Response<TransactionResponse>,
                ) {
                    if (response.isSuccessful) {
                        val transactionResponse = response.body()
                        transactionResponse?.let {
                            val data = it.data
                            val todone = "todone"
                            val intent = Intent(this@BookingActivity, BookingDoneActivity::class.java)
                            intent.putExtra("todone", todone)
                            intent.putExtra("idTransaksiView", data.id)
                            intent.putExtra("idPet", data.hewan_id)
                            intent.putExtra("updetat", data.tanggal_pengambilan)
                            startActivity(intent)

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
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                showTimePickerDialog()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun setDateTime() {
        val dateTimeFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())

        textView.setOnClickListener {
            showDatePickerDialog()
        }

        val currentDateTime = calendar.time
        val formattedDateTime = dateTimeFormat.format(currentDateTime)
        textView.text = formattedDateTime
    }

    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val dateTimeFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
                val selectedDateTime = calendar.time
                val formattedDateTime = dateTimeFormat.format(selectedDateTime)
                textView.text = formattedDateTime
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun getCurrentDateTime(): String {
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDateTime = Calendar.getInstance().time
        return dateTimeFormat.format(currentDateTime)
    }

    private fun sendNotification(title: String, message: String) {
        val fcmToken = FirebaseMessaging.getInstance().token

        // Mengirim pesan ke token perangkat yang terdaftar dalam Firebase
        // Gantikan "FCM_TOKEN_PENERIMA" dengan token perangkat penerima yang valid
        // Gantikan "YOUR_SERVER_KEY" dengan kunci server Firebase Cloud Messaging Anda
        val serverKey = "ngirimNotifikasi"
        val fcmTokenPenerima = "nerimaNotifikasi"

        val jsonBody = JSONObject()
        val jsonNotification = JSONObject()

        jsonNotification.put("title", title)
        jsonNotification.put("body", message)

        jsonBody.put("to", fcmTokenPenerima)
        jsonBody.put("notification", jsonNotification)

        val request = object : JsonObjectRequest(
            Request.Method.POST,
            "https://fcm.googleapis.com/fcm/send",
            jsonBody,
            { response ->
                // Berhasil mengirim notifikasi
                Log.d(TAG, "Notifikasi berhasil dikirim")
            },
            { error ->
                // Gagal mengirim notifikasi
                Log.e(TAG, "Gagal mengirim notifikasi: ${error.message}")
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "key=$serverKey"
                return headers
            }
        }

        // Mengirim permintaan ke Firebase Cloud Messaging
        Volley.newRequestQueue(this).add(request)
    }
}