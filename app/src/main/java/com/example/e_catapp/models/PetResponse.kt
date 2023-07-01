package com.example.e_catapp.models

data class PetResponse(
    val `data`: List<Pet>,
    val message: String,
    val status: Boolean
)