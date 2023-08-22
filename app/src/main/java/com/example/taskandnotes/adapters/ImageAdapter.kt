package com.example.taskandnotes.adapters

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.taskandnotes.R
import java.io.File
import java.lang.Math.abs

class ImageAdapter(private val context: Context, private val regularImagePath: List<String>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    private var currentPos: Int = 0
    private lateinit var imageDialog: Dialog
    private lateinit var dialogImageView: ImageView
    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    
    
        init {
            imageView.setOnClickListener {
                showImageDialog(adapterPosition)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(itemView)
    }
    
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imagePath = regularImagePath[position]
    
    
        val imageUri = Uri.fromFile(File(imagePath))
    
        Glide.with(context)
            .load(imageUri)
            .into(holder.imageView)
    
    }
    
    override fun getItemCount(): Int {
        return  regularImagePath.size
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun showImageDialog(startPosition: Int) {
        currentPos = startPosition
    
        imageDialog = Dialog(context)
        imageDialog.setContentView(R.layout.dialog_image_view)
    
        dialogImageView = imageDialog.findViewById(R.id.dialog_image)
        val currentImageUri = Uri.fromFile(File(regularImagePath[currentPos]))
        dialogImageView.setImageURI(currentImageUri)
        Log.d("ImageAdapterDialog", "Dialog image should be loaded from path: $currentImageUri")
    
        val prevButton: ImageButton = imageDialog.findViewById(R.id.prev_button)
        val nextButton: ImageButton = imageDialog.findViewById(R.id.next_button)
        val deleteButton: ImageButton = imageDialog.findViewById(R.id.dele_button)
    
        prevButton.setOnClickListener {
            navigateToPreviousImage()
            updateNavigationButtons()
        }
    
        nextButton.setOnClickListener {
            navigateToNextImage()
            updateNavigationButtons()
        }
    
        deleteButton.setOnClickListener {
            if (regularImagePath is MutableList) {
                regularImagePath.removeAt(currentPos)
                notifyDataSetChanged()
            }
            imageDialog.dismiss()
        }
    
        // Initial update for navigation buttons
        updateNavigationButtons()
    
        imageDialog.show()
    }
    
    private fun updateNavigationButtons() {
        val prevButton: ImageButton = imageDialog.findViewById(R.id.prev_button)
        val nextButton: ImageButton = imageDialog.findViewById(R.id.next_button)
        
        when {
            regularImagePath.size == 1 -> {
                prevButton.visibility = View.GONE
                nextButton.visibility = View.GONE
            }
            currentPos == 0 -> {
                prevButton.visibility = View.GONE
                nextButton.visibility = View.VISIBLE
            }
            currentPos == regularImagePath.size - 1 -> {
                prevButton.visibility = View.VISIBLE
                nextButton.visibility = View.GONE
            }
            else -> {
                prevButton.visibility = View.VISIBLE
                nextButton.visibility = View.VISIBLE
            }
        }
    }
    
    
    private fun navigateToPreviousImage() {
        if (currentPos > 0) {
            currentPos--
            val previousImageUri = Uri.fromFile(File(regularImagePath[currentPos]))
            dialogImageView.setImageURI(previousImageUri) // Use setImageURI here
        }
    }
    
    private fun navigateToNextImage() {
        if (currentPos < regularImagePath.size - 1) {
            currentPos++
            val nextImageUri = Uri.fromFile(File(regularImagePath[currentPos]))
            dialogImageView.setImageURI(nextImageUri) // Use setImageURI here
        }
    }
}



