package com.mercu.intrain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Course(
    val id: String,
    val title: String,
    val description: String,
    val provider: String,
    val url: String,
    val created_at: String
): Parcelable
