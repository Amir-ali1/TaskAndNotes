package com.example.taskandnotes.data.repo

import androidx.lifecycle.LiveData
import com.example.taskandnotes.db.Note
import com.example.taskandnotes.db.NoteDao

class NoteRepository(private val noteDao: NoteDao) {
    
    val allNotes: LiveData<List<Note>> = noteDao.getNotes()
    
    
    suspend fun insert(note: Note){
        noteDao.insert(note)
    }
    
    suspend fun update(note: Note) {
        noteDao.update(note)
    }
    
    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }
    
    suspend fun deleteAll() {
        noteDao.deleteAll()
    }
    
    fun searchNotes(searchQuery: String): LiveData<List<Note>> {
        return noteDao.searchNotes("%$searchQuery%")
    }
    
    fun getNoteById(id: Long): LiveData<Note> {
        return noteDao.getNoteById(id)
    }
    val noteCount: LiveData<Int> = noteDao.getNotesCount()
    
}
