package com.example.quizapp.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.model.Question
import com.example.quizapp.data.model.QuizResult
import com.example.quizapp.domain.usecase.GetQuestionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuizViewModel(
    private val getQuestionsUseCase: GetQuestionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    private val userAnswers = mutableListOf<Int?>()
    private var currentStreak = 0
    private var longestStreak = 0

    fun loadQuestions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            getQuestionsUseCase().fold(
                onSuccess = { questions ->
                    _questions.value = questions
                    userAnswers.clear()
                    repeat(questions.size) { userAnswers.add(null) }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentQuestionIndex = 0
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
            )
        }
    }

    fun selectAnswer(answerIndex: Int) {
        val currentIndex = _uiState.value.currentQuestionIndex
        val currentQuestion = _questions.value.getOrNull(currentIndex) ?: return

        userAnswers[currentIndex] = answerIndex

        val isCorrect = answerIndex == currentQuestion.correctAnswer
        if (isCorrect) {
            currentStreak++
            longestStreak = maxOf(longestStreak, currentStreak)
        } else {
            currentStreak = 0
        }

        _uiState.value = _uiState.value.copy(
            selectedAnswer = answerIndex,
            showAnswer = true,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            isStreakActive = currentStreak >= 3
        )

        // Auto advance after 2 seconds
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            nextQuestion()
        }
    }

    fun skipQuestion() {
        currentStreak = 0
        _uiState.value = _uiState.value.copy(
            currentStreak = 0,
            isStreakActive = false
        )
        nextQuestion()
    }

    fun nextQuestion() {
        val currentIndex = _uiState.value.currentQuestionIndex
        if (currentIndex < _questions.value.size - 1) {
            _uiState.value = _uiState.value.copy(
                currentQuestionIndex = currentIndex + 1,
                selectedAnswer = null,
                showAnswer = false
            )
        } else {
            // Quiz completed
            _uiState.value = _uiState.value.copy(isQuizCompleted = true)
        }
    }

    fun getQuizResult(): QuizResult {
        val correctAnswers = userAnswers.zip(_questions.value)
            .count { (userAnswer, question) ->
                userAnswer != null && userAnswer == question.correctAnswer
            }
        val skippedQuestions = userAnswers.count { it == null }

        return QuizResult(
            totalQuestions = _questions.value.size,
            correctAnswers = correctAnswers,
            skippedQuestions = skippedQuestions,
            longestStreak = longestStreak,
            userAnswers = userAnswers.toList()
        )
    }

    fun restartQuiz() {
        currentStreak = 0
        longestStreak = 0
        userAnswers.clear()
        repeat(_questions.value.size) { userAnswers.add(null) }

        _uiState.value = QuizUiState(
            currentQuestionIndex = 0
        )
    }
}

data class QuizUiState(
    val isLoading: Boolean = false,
    val currentQuestionIndex: Int = 0,
    val selectedAnswer: Int? = null,
    val showAnswer: Boolean = false,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val isStreakActive: Boolean = false,
    val isQuizCompleted: Boolean = false,
    val error: String? = null
)

class QuizViewModelFactory(
    private val getQuestionsUseCase: GetQuestionsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(getQuestionsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}