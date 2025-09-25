package com.example.quizapp.data.api

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {

    fun getRetrofitInstance(): QuizApiInterface {
        val moshi = Moshi.Builder().build()

        return Retrofit.Builder()
            .baseUrl("https://gist.githubusercontent.com/dr-samrat/53846277a8fcb034e482906ccc0d12b2/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build().create(QuizApiInterface::class.java)
    }
}