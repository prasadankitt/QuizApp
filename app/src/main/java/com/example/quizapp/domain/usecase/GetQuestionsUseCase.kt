package com.example.quizapp.domain.usecase

import com.example.quizapp.data.model.Question
import com.example.quizapp.data.repository.QuizRepository

class GetQuestionsUseCase(private val repository: QuizRepository) {
    suspend operator fun invoke(): Result<List<Question>> {
        return repository.getQuestions()
    }
}