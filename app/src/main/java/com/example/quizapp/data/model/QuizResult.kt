package com.example.quizapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuizResult(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val skippedQuestions: Int,
    val longestStreak: Int,
    val userAnswers: List<Int?>
) : Parcelable {

    val scorePercentage: Int
        get() = if (totalQuestions > 0) (correctAnswers * 100) / totalQuestions else 0

}