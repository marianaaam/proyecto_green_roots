package com.example.green_roots.model

import com.example.green_roots.R

data class Product(
    val name: String,
    val price: Int,
    val imageResId: Int = R.drawable.ic_products,
    val imageUri: String = "",
    val description: String = "",
    val reason: String = "",
    val category: String = "",
    val company: String = "",

    )
