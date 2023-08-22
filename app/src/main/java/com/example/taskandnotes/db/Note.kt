package com.example.taskandnotes.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    
    @ColumnInfo(name = "note_date")
    var date: String = "",
    
    @ColumnInfo(name = "note_title")
    var title: String = "",
    
    @ColumnInfo(name = "note_body")
    var body: String = "",
    
    @ColumnInfo(name = "canvas_image_path")
    var canvasImagePath: ByteArray? = null,
    
    @ColumnInfo(name = "regular_image_path")
    var regularImagePath: List<String>,
    
    @ColumnInfo(name = "bullet_points")
    var bulletPoints: String = ""
)
