package com.example.quizapp.ui.result

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.quizapp.data.model.QuizResult
import com.example.quizapp.ui.quiz.QuizActivity
import com.example.quizapp.utils.fadeInAnimation
import com.example.quizapp.utils.scaleAnimation
import com.example.quizapp.utils.slideInFromBottom
import com.quizapp.R

class ResultActivity : AppCompatActivity() {

    private lateinit var scoreText: TextView
    private lateinit var correctAnswersText: TextView
    private lateinit var skippedQuestionsText: TextView
    private lateinit var longestStreakText: TextView
    private lateinit var restartButton: Button

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        initViews()

        val result = intent.getParcelableExtra<QuizResult>("quiz_result")
        result?.let {
            animateResultsDisplay(it)
        }
    }

    private fun initViews() {
        scoreText = findViewById(R.id.scoreText)
        correctAnswersText = findViewById(R.id.correctAnswersText)
        skippedQuestionsText = findViewById(R.id.skippedQuestionsText)
        longestStreakText = findViewById(R.id.longestStreakText)
        restartButton = findViewById(R.id.restartButton)

        // Initially hide elements for animation
        scoreText.alpha = 0f
        correctAnswersText.alpha = 0f
        skippedQuestionsText.alpha = 0f
        longestStreakText.alpha = 0f
        restartButton.alpha = 0f

        restartButton.setOnClickListener {
            it.scaleAnimation(0.9f, 1.0f, 200)
            restartQuiz()
        }
    }

    private fun animateResultsDisplay(result: QuizResult) {
        // Animate score with counting effect
        animateScoreCounter(result.scorePercentage)

        // Staggered animation for stats
        handler.postDelayed({
            correctAnswersText.text = getString(
                R.string.correct_answers_format,
                result.correctAnswers,
                result.totalQuestions
            )
            correctAnswersText.fadeInAnimation(400)
        }, 800)

        handler.postDelayed({
            skippedQuestionsText.text = result.skippedQuestions.toString()
            skippedQuestionsText.fadeInAnimation(400)
        }, 1200)

        handler.postDelayed({
            longestStreakText.text = result.longestStreak.toString()
            longestStreakText.fadeInAnimation(400)
        }, 1600)

        handler.postDelayed({
            restartButton.slideInFromBottom(500)
        }, 2000)
    }

    private fun animateScoreCounter(targetScore: Int) {
        scoreText.fadeInAnimation(600)

        var currentScore = 0
        val increment = maxOf(1, targetScore / 30) // Animate over ~1 second

        val countingRunnable = object : Runnable {
            override fun run() {
                currentScore += increment
                if (currentScore >= targetScore) {
                    currentScore = targetScore
                    scoreText.text = "$currentScore%"
                    scoreText.scaleAnimation(1.0f, 1.1f, 300, true)
                } else {
                    scoreText.text = "$currentScore%"
                    handler.postDelayed(this, 33) // ~30 FPS
                }
            }
        }

        handler.postDelayed(countingRunnable, 500)
    }

    private fun restartQuiz() {
        val intent = Intent(this, QuizActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}