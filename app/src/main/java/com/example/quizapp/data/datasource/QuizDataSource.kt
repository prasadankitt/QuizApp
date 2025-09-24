package com.example.quizapp.data.datasource

import com.example.quizapp.data.model.Question

class QuizDataSource {

    // Sample JSON data - In real app, this would come from API
    private val jsonData = """
    {
      "questions": [
        {
          "id": 1,
          "question": "What is the capital of France?",
          "options": ["London", "Berlin", "Paris", "Madrid"],
          "correctAnswer": 2,
          "explanation": "Paris is the capital and largest city of France."
        },
        {
          "id": 2,
          "question": "Which planet is known as the Red Planet?",
          "options": ["Venus", "Mars", "Jupiter", "Saturn"],
          "correctAnswer": 1,
          "explanation": "Mars is called the Red Planet due to iron oxide on its surface."
        },
        {
          "id": 3,
          "question": "What is 15 + 28?",
          "options": ["41", "43", "45", "47"],
          "correctAnswer": 1,
          "explanation": "15 + 28 = 43"
        },
        {
          "id": 4,
          "question": "Who painted the Mona Lisa?",
          "options": ["Vincent van Gogh", "Leonardo da Vinci", "Pablo Picasso", "Claude Monet"],
          "correctAnswer": 1,
          "explanation": "Leonardo da Vinci painted the Mona Lisa between 1503-1519."
        },
        {
          "id": 5,
          "question": "What is the largest ocean on Earth?",
          "options": ["Atlantic Ocean", "Indian Ocean", "Arctic Ocean", "Pacific Ocean"],
          "correctAnswer": 3,
          "explanation": "The Pacific Ocean is the largest ocean, covering about 46% of Earth's water surface."
        },
        {
          "id": 6,
          "question": "In which year did World War II end?",
          "options": ["1944", "1945", "1946", "1947"],
          "correctAnswer": 1,
          "explanation": "World War II ended in 1945 with Japan's surrender in September."
        },
        {
          "id": 7,
          "question": "What is the chemical symbol for gold?",
          "options": ["Go", "Gd", "Au", "Ag"],
          "correctAnswer": 2,
          "explanation": "Au is the chemical symbol for gold, from the Latin word 'aurum'."
        },
        {
          "id": 8,
          "question": "Which is the smallest country in the world?",
          "options": ["Monaco", "Vatican City", "San Marino", "Liechtenstein"],
          "correctAnswer": 1,
          "explanation": "Vatican City is the smallest country with an area of just 0.17 square miles."
        },
        {
          "id": 9,
          "question": "What is the speed of light in vacuum?",
          "options": ["299,792,458 m/s", "300,000,000 m/s", "299,000,000 m/s", "298,792,458 m/s"],
          "correctAnswer": 0,
          "explanation": "The speed of light in vacuum is exactly 299,792,458 meters per second."
        },
        {
          "id": 10,
          "question": "Which programming language is Android primarily built with?",
          "options": ["Swift", "Java/Kotlin", "Python", "C++"],
          "correctAnswer": 1,
          "explanation": "Android apps are primarily developed using Java and Kotlin languages."
        }
      ]
    }
    """

    suspend fun getQuestions(): List<Question> {
        return try {
            // Simulate network delay
            kotlinx.coroutines.delay(1500)

            val jsonObject = org.json.JSONObject(jsonData)
            val questionsArray = jsonObject.getJSONArray("questions")
            val questions = mutableListOf<Question>()

            for (i in 0 until questionsArray.length()) {
                val questionObj = questionsArray.getJSONObject(i)
                val optionsArray = questionObj.getJSONArray("options")
                val options = mutableListOf<String>()

                for (j in 0 until optionsArray.length()) {
                    options.add(optionsArray.getString(j))
                }

                questions.add(
                    Question(
                        id = questionObj.getInt("id"),
                        question = questionObj.getString("question"),
                        options = options,
                        correctAnswer = questionObj.getInt("correctAnswer"),
                        explanation = questionObj.optString("explanation")
                    )
                )
            }
            questions
        } catch (e: Exception) {
            emptyList()
        }
    }
}