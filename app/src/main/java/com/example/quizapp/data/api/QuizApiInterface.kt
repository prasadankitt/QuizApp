package com.example.quizapp.data.api

import com.example.quizapp.data.model.Question
import retrofit2.Response
import retrofit2.http.GET

interface QuizApiInterface {
    @GET("raw")
    suspend fun getQuestions() : Response<List<Question>>
}