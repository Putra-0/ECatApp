package com.example.e_catapp.models

data class Pet(
    val berat: String,
    val created_at: String,
    val description: String,
    val id: Int,
    val images: List<Image>,
    val jenis_kelamin: String,
    val nama_hewan: String,
    val status: String,
    val harga: String,
    val status_vaksin: String,
    val type_id: Int,
    val type: Jenis,
    val umur: String,
    val updated_at: String
)