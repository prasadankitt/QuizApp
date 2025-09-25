package com.example.quizapp.data.datasource

import com.example.quizapp.data.model.Question

class QuizDataSource {
    private val jsonData = """
    {
      "questions": [
        {
          "id": 1,
          "question": "What is the capital of France?",
          "options": ["London", "Berlin", "Paris", "Madrid"],
          "correctOptionIndex": 2
        },
        {
          "id": 2,
          "question": "Which planet is known as the Red Planet?",
          "options": ["Venus", "Mars", "Jupiter", "Saturn"],
          "correctOptionIndex": 1
        },
        {
          "id": 3,
          "question": "What is 15 + 28?",
          "options": ["41", "43", "45", "47"],
          "correctOptionIndex": 1
        },
        {
          "id": 4,
          "question": "Who painted the Mona Lisa?",
          "options": ["Vincent van Gogh", "Leonardo da Vinci", "Pablo Picasso", "Claude Monet"],
          "correctOptionIndex": 1
        },
        {
          "id": 5,
          "question": "What is the largest ocean on Earth?",
          "options": ["Atlantic Ocean", "Indian Ocean", "Arctic Ocean", "Pacific Ocean"],
          "correctOptionIndex": 3
        },
        {
          "id": 6,
          "question": "In which year did World War II end?",
          "options": ["1944", "1945", "1946", "1947"],
          "correctOptionIndex": 1
        },
        {
          "id": 7,
          "question": "What is the chemical symbol for gold?",
          "options": ["Go", "Gd", "Au", "Ag"],
          "correctOptionIndex": 2
        },
        {
          "id": 8,
          "question": "Which is the smallest country in the world?",
          "options": ["Monaco", "Vatican City", "San Marino", "Liechtenstein"],
          "correctOptionIndex": 1
        },
        {
          "id": 9,
          "question": "What is the speed of light in vacuum?",
          "options": ["299,792,458 m/s", "300,000,000 m/s", "299,000,000 m/s", "298,792,458 m/s"],
          "correctOptionIndex": 0
        },
        {
          "id": 10,
          "question": "Which programming language is Android primarily built with?",
          "options": ["Swift", "Java/Kotlin", "Python", "C++"],
          "correctOptionIndex": 1
        }
      ]
    }
    """

    fun getQuestions(): List<Question> {
        return try {

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
                        correctOptionIndex = questionObj.getInt("correctOptionIndex")
                    )
                )
            }
            questions
        } catch (e: Exception) {
            emptyList()
        }
    }
}