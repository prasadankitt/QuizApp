package com.example.quizapp.di

import android.content.Context
import com.example.quizapp.data.datasource.QuizDataSource
import com.example.quizapp.data.repository.QuizRepository
import com.example.quizapp.domain.usecase.GetQuestionsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideQuizDataSource(): QuizDataSource = QuizDataSource()

    @Provides
    @Singleton
    fun provideQuizRepository(dataSource: QuizDataSource,@ApplicationContext context: Context): QuizRepository =
        QuizRepository(dataSource, context)

    @Provides
    @Singleton
    fun provideGetQuestionsUseCase(repo: QuizRepository): GetQuestionsUseCase =
        GetQuestionsUseCase(repo)
}
