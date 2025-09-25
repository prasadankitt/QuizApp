# MCQ Quiz App

A modern Android quiz application built with MVVM architecture and Material Design principles.

## Features

### Core Functionality
- **Splash Screen**: Beautiful loading screen with app branding
- **Question Flow**: Display questions with 4 multiple choice options
- **Auto-Advance**: Automatically moves to next question after 2 seconds
- **Skip Functionality**: Allow users to skip difficult questions
- **Streak System**: Track consecutive correct answers with visual badge
- **Results Screen**: Comprehensive statistics and restart option

### Technical Features
- **MVVM Architecture**: Clean separation of concerns
- **Modern UI**: Material Design 3 components
- **Animations**: Smooth transitions and micro-interactions
- **Gesture Support**: Swipe navigation during quiz
- **State Management**: Robust state handling with StateFlow
- **Error Handling**: Graceful error handling throughout the app

## Architecture

### Data Layer
- `QuizDataSource`: Handles JSON parsing and data retrieval
- `QuizRepository`: Manages data operations with error handling
- `Question` & `QuizResult`: Data models

### Domain Layer
- `GetQuestionsUseCase`: Business logic for retrieving questions

### Presentation Layer
- `QuizViewModel`: Manages UI state and business logic
- Activities with proper lifecycle management
- Modern UI components with animations

## Project Structure

```
app/src/main/java/com/quizapp/
├── data/
│   ├── model/
│   ├── repository/
|   ├── api/
│   └── datasource/
|   
├── domain/
│   └── usecase/
├── utils/
|
├── ui/
|   ├── splash/
|   ├── quiz/
|   └── result/
|
├── di/
|
└── QuizApp
```

## Key Implementation Details

### Streak Logic
- Tracks consecutive correct answers
- Badge lights up at 3+ streak
- Resets on wrong answers or skips
- Visual feedback with animations

### Question Navigation
- Automatic 2-second advance after answer selection
- Skip button for immediate advance
- Swipe gesture support
- Progress tracking

### Results Screen
- Score percentage calculation
- Detailed statistics (correct/total, skipped, longest streak)
- Restart functionality
- Engaging UI with cards and icons

### Modern Android Practices
- ViewBinding for type-safe view access
- Coroutines for asynchronous operations
- StateFlow for reactive UI updates
- Material Design components
- Splash Screen API
- Gesture detection

## Installation

1. Clone the repository
2. Open in Android Studio
3. Build and run the project
4. Ensure minimum SDK 24 (Android 7.0)

## Dependencies

- **Core**: AndroidX Core KTX, AppCompat, Material Components
- **Architecture**: Lifecycle components, ViewModels
- **Async**: Kotlin Coroutines
- **UI**: CardView, SplashScreen API
- **Testing**: JUnit, Espresso

## Design Highlights

- **Color Scheme**: Modern purple-based palette with accessibility
- **Typography**: Clean, readable fonts with proper hierarchy
- **Spacing**: Consistent 8dp grid system
- **Animations**: Subtle micro-interactions for engagement
- **Accessibility**: Proper contrast ratios and semantic markup

## Future Enhancements

- Network API integration
- Question categories and difficulty levels
- User profiles and score history
- Social features and leaderboards
- Offline mode with local storage
- Voice narration for accessibility

---

**Developed with ❤️ using Modern Android Development practices**
