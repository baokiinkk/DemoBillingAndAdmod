package com.baokiin.demobillingandadmod.model

data class Purchase(
    val productId: String,
    val type: String,
    val price: String,
    val price_amount_micros: String,
    val title: String,
    val description: String,
    val skuDetailsToken: String
)
