package com.example.taskandnotes.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {
    
    @Query("SELECT * from note_table ORDER BY note_date DESC")
    fun getNotes(): LiveData<List<Note>>
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)
    
    @Update
    suspend fun update(note: Note)
    
    @Delete
    suspend fun delete(note: Note)
    
    @Query("DELETE FROM note_table")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM note_table")
    fun getNotesCount(): LiveData<Int>
    
    
    @Query("SELECT * FROM note_table WHERE id = :id")
    fun getNoteById(id: Long): LiveData<Note>
    
    
    @Query("SELECT * FROM note_table WHERE note_title LIKE :searchQuery")
    fun searchNotes(searchQuery: String): LiveData<List<Note>>
}
