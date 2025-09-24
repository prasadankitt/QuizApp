package com.example.quizapp.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.quizapp.data.datasource.QuizDataSource
import com.example.quizapp.data.repository.QuizRepository
import com.example.quizapp.domain.usecase.GetQuestionsUseCase
import com.example.quizapp.ui.quiz.QuizActivity
import com.example.quizapp.ui.quiz.QuizViewModel
import com.example.quizapp.ui.quiz.QuizViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

class SplashActivity : AppCompatActivity() {

    private val quizViewModel: QuizViewModel by viewModels {
        QuizViewModelFactory(
            GetQuestionsUseCase(
                QuizRepository(QuizDataSource())
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Keep splash screen visible while loading
        splashScreen.setKeepOnScreenCondition { true }

        // Load questions
        quizViewModel.loadQuestions()

        // Navigate to quiz after loading
        lifecycleScope.launch {
            delay(2000) // Minimum splash duration
            quizViewModel.questions.collect { questions ->
                if (questions.isNotEmpty()) {
                    startActivity(Intent(this@SplashActivity, QuizActivity::class.java))
                    finish()
                }
            }
        }
    }
}