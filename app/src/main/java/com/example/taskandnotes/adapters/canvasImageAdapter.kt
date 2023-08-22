package com.example.taskandnotes.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskandnotes.R
import com.bumptech.glide.Glide

class CanvasImageAdapter(private val canvasImage: MutableList<ByteArray>) : RecyclerView.Adapter<CanvasImageAdapter.CanvasImageViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CanvasImageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.canvas_image_item, parent, false)
        return CanvasImageViewHolder(itemView)
    }
    
    override fun onBindViewHolder(holder: CanvasImageViewHolder, position: Int) {
        val imageBytes = canvasImage[position]
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        Glide.with(holder.canvasImageView.context)
            .load(bitmap)
            .into(holder.canvasImageView)
    
    }
    
    override fun getItemCount() = canvasImage.size
    fun addBitmap(bitmapByteArray: ByteArray) {
        canvasImage.add(bitmapByteArray)
        notifyDataSetChanged()
    }
    
    class CanvasImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val canvasImageView: ImageView = itemView.findViewById(R.id.canvas_image)
    }
}
