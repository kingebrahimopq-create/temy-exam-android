package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TemyDao {

    // --- Courses Management ---
    @Query("SELECT * FROM courses_table ORDER BY code ASC")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity)

    @Query("DELETE FROM courses_table WHERE code = :courseCode")
    suspend fun deleteCourseByCode(courseCode: String)

    // --- Questions Management ---
    @Query("SELECT * FROM questions_table ORDER BY id ASC")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions_table WHERE courseCode = :courseCode ORDER BY id ASC")
    fun getQuestionsForCourse(courseCode: String): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity)

    @Query("DELETE FROM questions_table WHERE id = :id")
    suspend fun deleteQuestionById(id: Int)

    @Query("DELETE FROM questions_table WHERE courseCode = :courseCode")
    suspend fun deleteQuestionsByCourse(courseCode: String)

    // --- Solved Questions ---
    @Query("SELECT * FROM solved_questions ORDER BY solvedAt DESC")
    fun getAllSolvedQuestions(): Flow<List<SolvedQuestionEntity>>

    @Query("SELECT * FROM solved_questions WHERE courseCode = :courseCode")
    fun getSolvedQuestionsForCourse(courseCode: String): Flow<List<SolvedQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSolvedQuestion(solved: SolvedQuestionEntity)

    @Query("DELETE FROM solved_questions")
    suspend fun clearSolvedQuestions()

    @Query("DELETE FROM solved_questions WHERE courseCode = :courseCode")
    suspend fun clearSolvedQuestionsForCourse(courseCode: String)

    // --- Bookmarks ---
    @Query("SELECT * FROM bookmarked_questions ORDER BY bookmarkedAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkedQuestionEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarked_questions WHERE id = :questionId)")
    fun isQuestionBookmarked(questionId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkedQuestionEntity)

    @Query("DELETE FROM bookmarked_questions WHERE id = :questionId")
    suspend fun deleteBookmarkById(questionId: Int)

    // --- Course Progress ---
    @Query("SELECT * FROM course_progress ORDER BY lastAccessed DESC")
    fun getCourseProgressList(): Flow<List<CourseProgressEntity>>

    @Query("SELECT * FROM course_progress WHERE courseCode = :courseCode LIMIT 1")
    suspend fun getCourseProgress(courseCode: String): CourseProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourseProgress(progress: CourseProgressEntity)

    @Query("DELETE FROM course_progress WHERE courseCode = :courseCode")
    suspend fun deleteCourseProgress(courseCode: String)

    // --- Lecture Notes & Summaries ---
    @Query("SELECT * FROM lecture_notes ORDER BY createdAt DESC")
    fun getAllLectureNotes(): Flow<List<LectureNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLectureNote(note: LectureNoteEntity)

    @Query("DELETE FROM lecture_notes WHERE id = :id")
    suspend fun deleteLectureNoteById(id: Int)
}
