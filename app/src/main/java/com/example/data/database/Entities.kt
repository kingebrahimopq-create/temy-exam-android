package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses_table")
data class CourseEntity(
    @PrimaryKey val code: String,
    val titleAr: String,
    val titleEn: String,
    val level: String, // e.g. "الفرقة الأولى"
    val term: String,  // e.g. "الترم الثاني"
    val totalQuestions: Int,
    val type: String, // e.g. "شرح مرئي متحرك"
    val isReady: Boolean = true,
    val colorHex: String
)

@Entity(tableName = "questions_table")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val courseCode: String,
    val text: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctIndex: Int,
    val explanation: String,
    val explanationVariant: String, // "MATH", "SCHEMA", "DIAGRAM", "FLOW"
    val explanationSteps: String // Pipe-separated string: "Step 1|Step 2"
)

@Entity(tableName = "solved_questions")
data class SolvedQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val courseCode: String,
    val questionId: Int,
    val selectedOption: Int,
    val isCorrect: Boolean,
    val solvedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookmarked_questions")
data class BookmarkedQuestionEntity(
    @PrimaryKey val id: Int, // Represents original questionId
    val courseCode: String,
    val courseName: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOptionIndex: Int,
    val explanation: String,
    val explanationType: String, // e.g. "VISUAL", "MATH", "FLOW"
    val explanationSteps: String, // Pipe-separated
    val bookmarkedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "course_progress")
data class CourseProgressEntity(
    @PrimaryKey val courseCode: String,
    val totalQuestions: Int,
    val solvedCount: Int,
    val correctCount: Int,
    val lastAccessed: Long = System.currentTimeMillis()
)

@Entity(tableName = "lecture_notes")
data class LectureNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val courseCode: String,
    val type: String, // "SUMMARY" (ملخص) or "HANDOUT" (ملزمة) or "NOTES" (ملاحظات)
    val content: String,
    val description: String,
    val fileSize: String = "150 KB",
    val isDownloaded: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
