package com.example.e_catapp.models

data class TransactionResponse(
    val `data`: Transaction,
    val message: String,
    val status: Boolean
)