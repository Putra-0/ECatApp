package com.example.e_catapp.models

data class UsersResponse(
    val `data`: List<Data>,
    val message: String,
    val status: Boolean
)