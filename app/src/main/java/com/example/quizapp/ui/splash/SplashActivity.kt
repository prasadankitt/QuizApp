package com.example.quizapp.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.quizapp.ui.quiz.QuizActivity
import com.example.quizapp.ui.quiz.QuizViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val quizViewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { true }

        quizViewModel.loadQuestions()

        lifecycleScope.launch {
            delay(2000)
            quizViewModel.questions.collect { questions ->
                if (questions.isNotEmpty()) {
                    startActivity(Intent(this@SplashActivity, QuizActivity::class.java))
                    finish()
                }
            }
        }
    }
}