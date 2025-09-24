package com.example.quizapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int, // Index of correct answer (0-3)
    val explanation: String? = null
) : Parcelable