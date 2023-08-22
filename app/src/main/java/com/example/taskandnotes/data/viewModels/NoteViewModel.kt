package com.example.taskandnotes.data.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskandnotes.data.repo.NoteRepository
import com.example.taskandnotes.db.Note
import com.example.taskandnotes.db.NoteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    
     private val repository: NoteRepository
    val allNotes: LiveData<List<Note>>
    val displayedNotes = MediatorLiveData<List<Note>>()
    
    
    
    
    init {
        val notesDao = NoteDatabase.getDatabase(application, viewModelScope).noteDao
        repository = NoteRepository(notesDao)
        allNotes = repository.allNotes
        
    }
    val noteCount: LiveData<Int> = repository.noteCount
    fun insert(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(note)
    }
    
    fun bulletPointsToString(bulletPoints: List<String>): String {
        return bulletPoints.joinToString(";;")
    }
    
    
    fun stringToBulletPoints(bulletString: String): List<String> {
        return bulletString.split(";;").filter { it.isNotBlank() }
    }
    
    fun getNoteById(id: Long): LiveData<Note> {
        return repository.getNoteById(id)
    }
    
    fun update(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(note)
    }
    
    fun delete(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(note)
    }
    
    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }
    
    fun searchNotes(searchQuery: String): LiveData<List<Note>> {
        return repository.searchNotes(searchQuery)
    }
    
    fun searchForNotes(query: String) {
        val searchResult = searchNotes(query)
        displayedNotes.addSource(searchResult) {
            displayedNotes.value = it
            displayedNotes.removeSource(searchResult)
        }
    }
    
    fun loadAllNotes() {
        displayedNotes.addSource(allNotes) {
            displayedNotes.value = it
            displayedNotes.removeSource(allNotes)
        }
    }
    
    
}
