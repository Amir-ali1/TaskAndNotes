@file:Suppress("DEPRECATION")

package com.example.taskandnotes.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.taskandnotes.R
import com.example.taskandnotes.data.viewModels.NoteViewModel
import com.example.taskandnotes.db.Note
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.ByteArrayOutputStream

class CanvasActivity : AppCompatActivity() {
    
    private lateinit var canvasView: CustomCanvasView
    private lateinit var viewModel: NoteViewModel
    private val colorMap = mapOf(
        Color.BLACK   to "Black",
        Color.BLUE    to "Blue",
        Color.RED     to "Red",
        Color.GREEN   to "Green",
        Color.GRAY    to "Gray",
        Color.YELLOW  to "Yellow",
        Color.DKGRAY  to "Dark Gray",
        Color.CYAN    to "Cyan",
        Color.MAGENTA to "Magenta",
        Color.LTGRAY  to "Light Gray"
    )
    
    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canvas)
    
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]
        canvasView = findViewById(R.id.drawingCanvas)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation)              
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.navigation_pencil -> {
                    showColorPickerDialog()
                }
                R.id.navigation_save -> {
                    saveDrawing()
                }
                R.id.navigation_undo -> {
                    canvasView.undo()
                }
                R.id.navigation_redo -> {
                    canvasView.redo()
                }
                R.id.navigation_eraser -> {
                
                }
            }
            true
        }
      
    }
    private fun showColorPickerDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose a color")
        
        val colorNames = colorMap.values.toList()
        
        val colorsAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            colorNames
        )
        
        builder.setAdapter(colorsAdapter) { _, which ->
            val selectedColorName = colorNames[which]
            val selectedColor = colorMap.entries.first { it.value == selectedColorName }.key
            canvasView.setCurrentColor(selectedColor)
        }
        
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        
        builder.create().show()
    }

    
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream)
        return stream.toByteArray()
    }
    
    private fun saveDrawing() {
        val bitmap = canvasView.getBitmap()
        val byteArray = bitmapToByteArray(bitmap)
        
        val note = Note(
            canvasImagePath = byteArray,
            regularImagePath = listOf()
        )
        
        viewModel.insert(note)
        
        val resultIntent = Intent().apply {
            putExtra("SAVED_CANVAS_IMAGE", byteArray)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }
    
}
