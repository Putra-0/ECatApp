package com.example.e_catapp.models

data class TransactionShowResponse(
    val `data`: List<Transaction>,
    val message: String,
    val status: Boolean
)