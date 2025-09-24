package com.example.quizapp.data.repository

import com.example.quizapp.data.datasource.QuizDataSource
import com.example.quizapp.data.model.Question

class QuizRepository(private val dataSource: QuizDataSource) {

    suspend fun getQuestions(): Result<List<Question>> {
        return try {
            val questions = dataSource.getQuestions()
            if (questions.isNotEmpty()) {
                Result.success(questions)
            } else {
                Result.failure(Exception("No questions found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}