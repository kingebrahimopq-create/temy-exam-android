package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.*
import com.example.data.repository.TemyRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TemyViewModel(application: Application) : AndroidViewModel(application) {

    val repository: TemyRepository

    // --- Dynamic Room Database Flows ---
    val allCourses = MutableStateFlow<List<CourseEntity>>(emptyList())
    val allQuestions = MutableStateFlow<List<QuestionEntity>>(emptyList())
    val allSolvedQuestions = MutableStateFlow<List<SolvedQuestionEntity>>(emptyList())
    val allBookmarks = MutableStateFlow<List<BookmarkedQuestionEntity>>(emptyList())
    val allCourseProgress = MutableStateFlow<List<CourseProgressEntity>>(emptyList())
    val allLectureNotes = MutableStateFlow<List<LectureNoteEntity>>(emptyList())

    // --- Academic Filters ---
    val selectedMajor = MutableStateFlow("نظم المعلومات الإدارية")
    val selectedYear = MutableStateFlow("الفرقة الأولى")
    val selectedTerm = MutableStateFlow("الترم الثاني")

    // --- Active Practice / Quiz State for Students ---
    val activeCourse = MutableStateFlow<CourseEntity?>(null)
    val activeQuestions = MutableStateFlow<List<QuestionEntity>>(emptyList())
    val currentQuestionIndex = MutableStateFlow(0)
    val selectedOptionIndex = MutableStateFlow<Int?>(null)
    val isAnswerSubmitted = MutableStateFlow(false)
    val isCurrentCorrect = MutableStateFlow(false)
    val bookmarkedStatus = MutableStateFlow(false)

    // --- Live Calculated Stats Counters ---
    val totalSolvedCount = allSolvedQuestions.map { list ->
        list.map { it.questionId }.distinct().size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val correctRate = allSolvedQuestions.map { list ->
        if (list.isEmpty()) 0 else {
            val uniqueSolvedIds = list.map { it.questionId }.distinct()
            val correctCount = uniqueSolvedIds.count { qId ->
                list.filter { it.questionId == qId }.maxByOrNull { it.solvedAt }?.isCorrect == true
            }
            (correctCount * 100) / uniqueSolvedIds.size
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedCoursesCount = combine(allCourses, allSolvedQuestions) { courses, solved ->
        var complete = 0
        for (c in courses) {
            val courseSolved = solved.filter { it.courseCode == c.code }.map { it.questionId }.distinct().size
            if (courseSolved >= c.totalQuestions && c.totalQuestions > 0) {
                complete++
            }
        }
        complete
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalScorePoints = allSolvedQuestions.map { list ->
        val uniqueSolvedIds = list.map { it.questionId }.distinct()
        uniqueSolvedIds.count { qId ->
            list.filter { it.questionId == qId }.maxByOrNull { it.solvedAt }?.isCorrect == true
        } * 10
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        val database = TemyDatabase.getDatabase(application)
        repository = TemyRepository(database.temyDao())

        // Sync and Collect Database Flows
        viewModelScope.launch {
            repository.checkAndSeedData()
            repository.allCourses.collect { allCourses.value = it }
        }
        viewModelScope.launch {
            repository.allQuestions.collect { allQuestions.value = it }
        }
        viewModelScope.launch {
            repository.allSolvedQuestions.collect { allSolvedQuestions.value = it }
        }
        viewModelScope.launch {
            repository.allBookmarks.collect { allBookmarks.value = it }
        }
        viewModelScope.launch {
            repository.allCourseProgress.collect { allCourseProgress.value = it }
        }
        viewModelScope.launch {
            repository.allLectureNotes.collect { allLectureNotes.value = it }
        }

        // Listen for current question bookmark status
        viewModelScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            combine(currentQuestionIndex, activeQuestions) { index, questions ->
                if (questions.isNotEmpty() && index in questions.indices) {
                    questions[index].id
                } else null
            }.flatMapLatest { qId ->
                if (qId != null) repository.isBookmarked(qId) else flowOf(false)
            }.collect { isBooked ->
                bookmarkedStatus.value = isBooked
            }
        }
    }

    // --- Admin Dashboard Methods (إداريات المدير) ---
    fun addCourse(
        code: String,
        titleAr: String,
        titleEn: String,
        level: String,
        term: String,
        totalQuestions: Int,
        type: String,
        colorHex: String
    ) {
        viewModelScope.launch {
            repository.insertCourse(
                CourseEntity(
                    code = code,
                    titleAr = titleAr,
                    titleEn = titleEn,
                    level = level,
                    term = term,
                    totalQuestions = totalQuestions,
                    type = type,
                    isReady = true,
                    colorHex = colorHex
                )
            )
        }
    }

    fun deleteCourse(code: String) {
        viewModelScope.launch {
            repository.deleteCourse(code)
        }
    }

    fun addQuestion(
        courseCode: String,
        text: String,
        optionA: String,
        optionB: String,
        optionC: String,
        optionD: String,
        correctIndex: Int,
        explanation: String,
        explanationVariant: String,
        explanationSteps: String
    ) {
        viewModelScope.launch {
            repository.insertQuestion(
                QuestionEntity(
                    courseCode = courseCode,
                    text = text,
                    optionA = optionA,
                    optionB = optionB,
                    optionC = optionC,
                    optionD = optionD,
                    correctIndex = correctIndex,
                    explanation = explanation,
                    explanationVariant = explanationVariant,
                    explanationSteps = explanationSteps
                )
            )
            // Automatically recount and update totalQuestions for the associated course
            repository.getQuestionsForCourse(courseCode).firstOrNull()?.let { questions ->
                val course = allCourses.value.find { it.code == courseCode }
                if (course != null) {
                    repository.insertCourse(
                        course.copy(totalQuestions = questions.size + 1)
                    )
                }
            }
        }
    }

    fun deleteQuestion(id: Int, courseCode: String) {
        viewModelScope.launch {
            repository.deleteQuestion(id)
            // Re-sync correct total questions count
            repository.getQuestionsForCourse(courseCode).firstOrNull()?.let { questions ->
                val course = allCourses.value.find { it.code == courseCode }
                if (course != null) {
                    repository.insertCourse(
                        course.copy(totalQuestions = (questions.size - 1).coerceAtLeast(0))
                    )
                }
            }
        }
    }

    // --- Student Section / Quiz Methods (بوابة الطلاب) ---

    fun startQuiz(course: CourseEntity) {
        activeCourse.value = course
        viewModelScope.launch {
            repository.getQuestionsForCourse(course.code).collect { list ->
                activeQuestions.value = list
                currentQuestionIndex.value = 0
                selectedOptionIndex.value = null
                isAnswerSubmitted.value = false
                isCurrentCorrect.value = false
            }
        }
    }

    fun selectOption(index: Int) {
        if (!isAnswerSubmitted.value) {
            selectedOptionIndex.value = index
        }
    }

    fun submitAnswer() {
        val course = activeCourse.value ?: return
        val questionsList = activeQuestions.value
        val index = currentQuestionIndex.value
        val optionIndex = selectedOptionIndex.value ?: return

        if (index in questionsList.indices && !isAnswerSubmitted.value) {
            val question = questionsList[index]
            val isCorrect = optionIndex == question.correctIndex
            isCurrentCorrect.value = isCorrect
            isAnswerSubmitted.value = true

            // Save in DB
            viewModelScope.launch {
                repository.saveSolvedQuestion(
                    courseCode = course.code,
                    questionId = question.id,
                    selectedOption = optionIndex,
                    isCorrect = isCorrect,
                    totalQuestionsInCourse = course.totalQuestions
                )
            }
        }
    }

    fun nextQuestion() {
        if (currentQuestionIndex.value < activeQuestions.value.size - 1) {
            currentQuestionIndex.value += 1
            selectedOptionIndex.value = null
            isAnswerSubmitted.value = false
            isCurrentCorrect.value = false
        }
    }

    fun previousQuestion() {
        if (currentQuestionIndex.value > 0) {
            currentQuestionIndex.value -= 1
            selectedOptionIndex.value = null
            isAnswerSubmitted.value = false
            isCurrentCorrect.value = false
        }
    }

    fun toggleBookmarkActive() {
        val questionsList = activeQuestions.value
        val index = currentQuestionIndex.value
        val course = activeCourse.value ?: return

        if (index in questionsList.indices) {
            val q = questionsList[index]
            viewModelScope.launch {
                repository.toggleBookmark(
                    questionId = q.id,
                    courseCode = q.courseCode,
                    courseName = course.titleAr,
                    questionText = q.text,
                    options = listOf(q.optionA, q.optionB, q.optionC, q.optionD),
                    correctOptionIndex = q.correctIndex,
                    explanation = q.explanation,
                    explanationType = q.explanationVariant,
                    explanationSteps = q.explanationSteps.split("|")
                )
            }
        }
    }

    fun addCustomNote(title: String, courseCode: String, type: String, content: String, description: String) {
        viewModelScope.launch {
            repository.insertLectureNote(
                LectureNoteEntity(
                    title = title,
                    courseCode = courseCode,
                    type = type,
                    content = content,
                    description = description
                )
            )
        }
    }

    fun removeNote(id: Int) {
        viewModelScope.launch {
            repository.deleteLectureNote(id)
        }
    }

    fun clearQuizHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
