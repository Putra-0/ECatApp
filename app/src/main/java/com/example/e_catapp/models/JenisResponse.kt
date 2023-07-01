package com.example.e_catapp.models

data class JenisResponse(
    val `data`: List<Jenis>,
    val message: String,
    val status: Boolean
)