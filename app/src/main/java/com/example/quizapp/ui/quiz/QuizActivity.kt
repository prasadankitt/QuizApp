package com.example.quizapp.ui.quiz

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.quizapp.data.datasource.QuizDataSource
import com.example.quizapp.data.model.Question
import com.example.quizapp.data.repository.QuizRepository
import com.example.quizapp.domain.usecase.GetQuestionsUseCase
import com.example.quizapp.ui.result.ResultActivity
import com.example.quizapp.utils.fadeInAnimation
import com.example.quizapp.utils.pulseAnimation
import com.example.quizapp.utils.scaleAnimation
import com.quizapp.R
import kotlinx.coroutines.launch
import kotlin.math.abs

class QuizActivity : AppCompatActivity() {

    private val viewModel: QuizViewModel by viewModels {
        QuizViewModelFactory(
            GetQuestionsUseCase(
                QuizRepository(QuizDataSource())
            )
        )
    }

    private lateinit var gestureDetector: GestureDetector

    // Views
    private lateinit var progressBar: ProgressBar
    private lateinit var questionCounter: TextView
    private lateinit var questionText: TextView
    private lateinit var optionsContainer: LinearLayout
    private lateinit var skipButton: Button
    private lateinit var streakBadge: TextView
    private lateinit var streakText: TextView

    private val optionButtons = mutableListOf<Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        initViews()
        setupGestureDetector()
        observeViewModel()

        // Load questions if not already loaded
        if (viewModel.questions.value.isEmpty()) {
            viewModel.loadQuestions()
        }
    }

    private fun initViews() {
        progressBar = findViewById(R.id.progressBar)
        questionCounter = findViewById(R.id.questionCounter)
        questionText = findViewById(R.id.questionText)
        optionsContainer = findViewById(R.id.optionsContainer)
        skipButton = findViewById(R.id.skipButton)
        streakBadge = findViewById(R.id.streakBadge)
        streakText = findViewById(R.id.streakText)

        // Create option buttons dynamically
        createOptionButtons()

        skipButton.setOnClickListener {
            viewModel.skipQuestion()
            it.scaleAnimation(0.9f, 1.0f, 150)
        }
    }

    private fun createOptionButtons() {
        optionButtons.clear()
        optionsContainer.removeAllViews()

        for (i in 0 until 4) {
            val button = Button(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 16, 0, 0)
                }
                textSize = 16f
                setPadding(32, 24, 32, 24)
                setBackgroundResource(R.drawable.option_button_selector)
                setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                isAllCaps = false

                // Add click animation
                setOnClickListener { view ->
                    if (!viewModel.uiState.value.showAnswer) {
                        view.scaleAnimation(0.95f, 1.0f, 200)
                        viewModel.selectAnswer(i)
                    }
                }
            }

            optionButtons.add(button)
            optionsContainer.addView(button)
        }
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
                    if (velocityX > 1000) { // Swipe right with sufficient velocity
                        val uiState = viewModel.uiState.value
                        if (uiState.showAnswer) {
                            // Manual advance if answer is already shown
                            viewModel.nextQuestion()
                            return true
                        }
                    }
                }
                return false
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
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

    private fun updateUI(questions: List<Question>, uiState: QuizUiState) {
        if (questions.isEmpty() || uiState.currentQuestionIndex >= questions.size) return

        val currentQuestion = questions[uiState.currentQuestionIndex]

        // Update progress with animation
        updateProgress(uiState.currentQuestionIndex + 1, questions.size)

        // Update question counter
        questionCounter.text = getString(
            R.string.question_counter_format,
            uiState.currentQuestionIndex + 1,
            questions.size
        )

        // Update question text with animation
        updateQuestionText(currentQuestion.question)

        // Update options
        updateOptions(currentQuestion, uiState)

        // Update streak display
        updateStreakDisplay(uiState)

        // Handle skip button visibility
        skipButton.visibility = if (uiState.showAnswer) View.GONE else View.VISIBLE
    }

    private fun updateProgress(current: Int, total: Int) {
        val targetProgress = (current * 100) / total
        ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, targetProgress).apply {
            duration = 300
            start()
        }
    }

    private fun updateQuestionText(question: String) {
        if (questionText.text != question) {
            questionText.fadeInAnimation(400)
            questionText.text = question
        }
    }

    private fun updateOptions(question: Question, uiState: QuizUiState) {
        for (i in optionButtons.indices) {
            val button = optionButtons[i]
            button.text = question.options[i]
            button.isEnabled = !uiState.showAnswer

            // Reset button appearance
            button.setBackgroundResource(R.drawable.option_button_selector)
            button.setTextColor(ContextCompat.getColor(this, R.color.text_primary))

            if (uiState.showAnswer) {
                when {
                    i == question.correctAnswer -> {
                        button.setBackgroundColor(ContextCompat.getColor(this, R.color.correct_answer))
                        button.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                        button.scaleAnimation(1.0f, 1.05f, 200, true)
                    }
                    i == uiState.selectedAnswer && i != question.correctAnswer -> {
                        button.setBackgroundColor(ContextCompat.getColor(this, R.color.wrong_answer))
                        button.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                    }
                }
            }
        }
    }

    private fun updateStreakDisplay(uiState: QuizUiState) {
        streakText.text = getString(R.string.streak_format, uiState.currentStreak)

        if (uiState.isStreakActive && streakBadge.background !=
            ContextCompat.getDrawable(this, R.drawable.streak_badge_active)) {

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

    override fun onBackPressed() {
        // Prevent back navigation during quiz
        // You can show a dialog to confirm exit if needed
        super.onBackPressed()
    }
}