@file:Suppress("DEPRECATION")

package com.example.taskandnotes.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskandnotes.R
import com.example.taskandnotes.adapters.NotesAdapter
import com.example.taskandnotes.data.viewModels.NoteViewModel
import com.example.taskandnotes.databinding.ActivityMainBinding
import com.example.taskandnotes.db.Note

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var viewModel: NoteViewModel
    private var inSelectionMode = false
    private val selectedItems = mutableSetOf<Note>()
    val YOUR_CANVAS_ACTIVITY_REQUEST_CODE = 1003
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        
        val searchContainer = binding.search1
        val searchView = binding.search
        binding.deleteButton.visibility = View.INVISIBLE
    
    
    
        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
            viewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
            
            
            
            notesAdapter = NotesAdapter(emptyList())
            binding.notesRecyclerView.adapter = notesAdapter
            
            viewModel.allNotes.observe(this, Observer { notes ->
                notesAdapter.setNotes(notes)
            })
            viewModel.displayedNotes.observe(this, Observer { notes ->
                notesAdapter.setNotes(notes)
            })
            
           
            binding.add.setOnClickListener() {
                val intent = Intent(this, AddNotes::class.java)
                startActivity(intent)
            }
            
            notesAdapter.setOnItemClickListener(object : NotesAdapter.OnItemClickListener {
                override fun onItemClick(note: Note) {
                    val intent = Intent(this@MainActivity, AddNotes::class.java)
                    intent.putExtra("NOTE_ID", note.id)
                    startActivity(intent)
                }
    
                override fun onEnterSelectionMode() {
                    binding.deleteButton.visibility = View.VISIBLE
                }
    
                override fun onExitSelectionMode() {
                    binding.deleteButton.visibility = View.INVISIBLE
                }
            })
            
            viewModel.noteCount.observe(this, Observer { count ->
                binding.total.text = " $count Notes"
            })
            
        
            val button: Button = findViewById(R.id.popup)
            button.setOnClickListener {
                val popupMenu = PopupMenu(this, it)
                popupMenu.inflate(R.menu.menu)
                
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu1 -> {
                            val intent = Intent(this, AddNotes::class.java)
                            intent.putExtra("SHOW_ADD_IMAGE_DIALOG", true)
                            startActivity(intent)
                            true
                        }
                        
                        R.id.menu2 -> {
                            val intent = Intent(this, CanvasActivity::class.java)
                            startActivityForResult(intent, YOUR_CANVAS_ACTIVITY_REQUEST_CODE)
                            true
                        }
                        
                        R.id.menu3 -> {
                            inSelectionMode = !inSelectionMode
                            notesAdapter.toggleSelectionMode(inSelectionMode)
                            true
                        }
                        
                        else -> false
                    }
                }
                popupMenu.show()
            }
    
        binding.deleteButton.setOnClickListener {
            val itemsToDelete = notesAdapter.getSelectedItems()
        
            if (itemsToDelete.isNotEmpty()) {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Delete Notes")
                alertDialog.setMessage("Are you sure you want to delete the selected notes?")
            
                alertDialog.setPositiveButton("Yes") { _, _ ->
                    for (note in itemsToDelete) {
                        viewModel.delete(note)
                    }
                    inSelectionMode = false
                    notesAdapter.toggleSelectionMode(false)
                }
            
                alertDialog.setNegativeButton("No") { dialog, _ ->
                    inSelectionMode = false
                    notesAdapter.toggleSelectionMode(false)
                    dialog.dismiss()
                }
            
                alertDialog.show()
            }
        }
    
       
    
    
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }
                
                override fun onQueryTextChange(newText: String?): Boolean {
                    if (!newText.isNullOrEmpty()) {
                        viewModel.searchForNotes(newText)
                    } else {
                        viewModel.loadAllNotes()
                    }
                    return true
                }
            })
        
        
        searchContainer.setOnClickListener {
            if (!searchView.isFocused) {
                searchView.isIconified = false
                searchView.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }
    
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (inSelectionMode) {
            inSelectionMode = false
            notesAdapter.toggleSelectionMode(false)
            return
        }
        
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Do you really want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                super.onBackPressed()
            }
            .setNegativeButton("No", null) // Do nothing if 'No' is pressed
            .create()
        
        alertDialog.show()
    }
}

