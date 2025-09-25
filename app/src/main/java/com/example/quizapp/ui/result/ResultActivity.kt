package com.example.quizapp.ui.result

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.example.quizapp.data.model.QuizResult
import com.example.quizapp.ui.quiz.QuizActivity
import com.example.quizapp.utils.fadeInAnimation
import com.example.quizapp.utils.scaleAnimation
import com.example.quizapp.utils.slideInFromBottom
import com.quizapp.R
import com.quizapp.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private var pendingAnimators = mutableListOf<ValueAnimator>()
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(LayoutInflater.from(this@ResultActivity))
        setContentView(binding.root)

        initViews()

        val result = intent.getParcelableExtra<QuizResult>("quiz_result")
        result?.let {
            animateResultsDisplay(it)
        }
    }

    private fun initViews() = with(binding) {
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

    private fun animateResultsDisplay(result: QuizResult) = with(binding) {
        // Animate score with counting effect
        animateScoreCounter(result.scorePercentage)

        // Staggered animation for stats using alpha animators with delays
        correctAnswersText.postDelayed({
            correctAnswersText.text = getString(
                R.string.correct_answers_format,
                result.correctAnswers,
                result.totalQuestions
            )
            correctAnswersText.fadeInAnimation(400)
        }, 800)

        skippedQuestionsText.postDelayed({
            skippedQuestionsText.text = result.skippedQuestions.toString()
            skippedQuestionsText.fadeInAnimation(400)
        }, 1200)

        longestStreakText.postDelayed({
            longestStreakText.text = result.longestStreak.toString()
            longestStreakText.fadeInAnimation(400)
        }, 1600)

        restartButton.postDelayed({
            restartButton.slideInFromBottom(500)
        }, 2000)
    }

    private fun animateScoreCounter(targetScore: Int) = with(binding) {
        scoreText.fadeInAnimation(600)

        val animator = ValueAnimator.ofInt(0, targetScore).apply {
            duration = 1000
            addUpdateListener {
                val value = it.animatedValue as Int
                scoreText.text = "$value%"
            }
            doOnEnd {
                scoreText.scaleAnimation(1.0f, 1.1f, 300, true)
            }
        }
        pendingAnimators.add(animator)
        scoreText.postDelayed({ animator.start() }, 500)
    }

    private fun restartQuiz() {
        val intent = Intent(this, QuizActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onDestroy() {
        pendingAnimators.forEach { it.cancel() }
        pendingAnimators.clear()
        super.onDestroy()
    }
}