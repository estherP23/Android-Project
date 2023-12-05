package com.example.market

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailFragmen(
    val imageUrl: String,
    val title: String,
    val price: Int,

    val seller: String,
    val sellerEmail: String,
    val content: String,
    val sell: Boolean
) : Parcelable {



    constructor() : this("", "", 0, "", "", "",true)
}
