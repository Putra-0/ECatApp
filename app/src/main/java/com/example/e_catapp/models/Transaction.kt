package com.example.e_catapp.models

data class Transaction(
    val created_at: String,
    val hewan: Pet,
    val hewan_id: Int,
    val id: Int,
    val status: String,
    val status_penerimaan: String,
    val tanggal_pengambilan: String,
    val updated_at: String,
    val user: User,
    val user_id: Int
)