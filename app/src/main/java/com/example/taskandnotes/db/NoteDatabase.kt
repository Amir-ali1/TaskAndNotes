package com.example.taskandnotes.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(StringListConverter::class)
abstract class NoteDatabase : RoomDatabase() {
    
    abstract val noteDao: NoteDao
    
    companion object {
        
        @Volatile
        private var INSTANCE: NoteDatabase? = null
        
        fun getDatabase(context: Context, scope: CoroutineScope): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
