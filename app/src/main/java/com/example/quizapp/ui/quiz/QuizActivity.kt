package com.example.quizapp.ui.quiz

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizapp.data.model.Question
import com.example.quizapp.ui.result.ResultActivity
import com.example.quizapp.utils.fadeInAnimation
import com.example.quizapp.utils.pulseAnimation
import com.example.quizapp.utils.scaleAnimation
import com.quizapp.R
import com.quizapp.databinding.ActivityQuizBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class QuizActivity : AppCompatActivity() {

    private val viewModel: QuizViewModel by viewModels()

    private lateinit var gestureDetector: GestureDetector
    private lateinit var binding: ActivityQuizBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(LayoutInflater.from(this@QuizActivity))
        setContentView(binding.root)

        setupOptionsRecycler()
        setupBackPressHandling()
        setupGestureDetector()
        observeViewModel()

        if (viewModel.questions.value.isEmpty()) {
            viewModel.loadQuestions()
        }

        binding.skipButton.setOnClickListener {
            viewModel.skipQuestion()
            it.scaleAnimation(0.9f, 1.0f, 150)
        }

    }

    private fun setupBackPressHandling() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Intentionally no-op to prevent back navigation during quiz
            }
        })
    }
    private lateinit var optionAdapter: OptionAdapter

    private fun setupOptionsRecycler() = with(binding){
        optionAdapter = OptionAdapter { index ->
            if (!viewModel.uiState.value.showAnswer) {
                viewModel.selectAnswer(index)
            }
        }
        optionsRecycler.layoutManager = LinearLayoutManager(this@QuizActivity)
        optionsRecycler.adapter = optionAdapter
        optionsRecycler.itemAnimator = null
    }

    private fun setupGestureDetector() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 != null && abs(velocityX) > abs(velocityY)) {
                    if (velocityX > 1000) {
                        val uiState = viewModel.uiState.value
                        if (uiState.showAnswer) {
                            viewModel.nextQuestion()
                            return true
                        }
                    }
                }
                return false
            }
        })
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.questions.collect { questions ->
                if (questions.isNotEmpty()) {
                    updateUI(questions, viewModel.uiState.value)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                updateUI(viewModel.questions.value, uiState)

                if (uiState.isQuizCompleted) {
                    navigateToResults()
                }
            }
        }
    }

    private fun updateUI(questions: List<Question>, uiState: QuizUiState) = with(binding) {
        if (questions.isEmpty() || uiState.currentQuestionIndex >= questions.size) return

        val currentQuestion = questions[uiState.currentQuestionIndex]

        updateProgress(uiState.currentQuestionIndex + 1, questions.size)

        questionCounter.text = getString(
            R.string.question_counter_format,
            uiState.currentQuestionIndex + 1,
            questions.size
        )
        updateQuestionText(currentQuestion.question)
        updateOptions(currentQuestion, uiState)
        updateStreakDisplay(uiState)
        skipButton.visibility = if (uiState.showAnswer) View.GONE else View.VISIBLE
    }

    private fun updateProgress(current: Int, total: Int) = with(binding) {
        val targetProgress = (current * 100) / total
        ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, targetProgress).apply {
            duration = 300
            start()
        }
    }

    private fun updateQuestionText(question: String) = with(binding) {
        if (questionText.text != question) {
            questionText.fadeInAnimation(400)
            questionText.text = question
        }
    }

    private fun updateOptions(question: Question, uiState: QuizUiState) {
        val items = (0 until 4).map { i ->
            OptionItem(
                text = question.options[i],
                index = i,
                isEnabled = !uiState.showAnswer,
                isCorrect = uiState.showAnswer && i == question.correctOptionIndex,
                isSelectedWrong = uiState.showAnswer && uiState.selectedAnswer == i && i != question.correctOptionIndex
            )
        }
        optionAdapter.submitList(items)
    }

    private fun updateStreakDisplay(uiState: QuizUiState) = with(binding) {
        streakText.text = getString(R.string.streak_format, uiState.currentStreak)

        if (uiState.isStreakActive && streakBadge.background !=
            ContextCompat.getDrawable(this@QuizActivity, R.drawable.streak_badge_active)) {

            streakBadge.setBackgroundResource(R.drawable.streak_badge_active)
            streakBadge.pulseAnimation(600)

        } else if (!uiState.isStreakActive) {
            streakBadge.setBackgroundResource(R.drawable.streak_badge_inactive)
        }
    }

    private fun navigateToResults() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("quiz_result", viewModel.getQuizResult())
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

}