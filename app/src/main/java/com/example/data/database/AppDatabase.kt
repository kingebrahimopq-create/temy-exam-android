package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CourseEntity::class,
        QuestionEntity::class,
        SolvedQuestionEntity::class,
        BookmarkedQuestionEntity::class,
        CourseProgressEntity::class,
        LectureNoteEntity::class
    ],
    version = 2, // Upgraded version to 2 for changes
    exportSchema = false
)
abstract class TemyDatabase : RoomDatabase() {
    abstract fun temyDao(): TemyDao

    companion object {
        @Volatile
        private var INSTANCE: TemyDatabase? = null

        fun getDatabase(context: Context): TemyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TemyDatabase::class.java,
                    "temy_academic_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
