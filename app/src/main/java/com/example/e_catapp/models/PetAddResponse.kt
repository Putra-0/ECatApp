package com.example.e_catapp.models

data class PetAddResponse(
    val data: Pet,
    val message: String,
    val status: Boolean
)