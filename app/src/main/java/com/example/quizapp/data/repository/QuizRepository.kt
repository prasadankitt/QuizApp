package com.example.quizapp.data.repository

import android.content.Context
import com.example.quizapp.data.api.RetrofitInstance
import com.example.quizapp.data.datasource.QuizDataSource
import com.example.quizapp.data.model.Question
import com.example.quizapp.utils.isInternetAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizRepository(private val dataSource: QuizDataSource, private val context: Context) {

    suspend fun getQuestions(): Result<List<Question>> = withContext(Dispatchers.IO) {
        return@withContext try {
            var questions = dataSource.getQuestions()
            if (isInternetAvailable(context.applicationContext)) {
                val response = RetrofitInstance.getRetrofitInstance().getQuestions()
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    questions = body
                }
            }
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