package com.example.taskandnotes.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskandnotes.R
import com.example.taskandnotes.db.Note

class NotesAdapter(private var notesList: List<Note>) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {
    private var inSelectionMode = false
    private val selectedItems = mutableSetOf<Note>()
    private var filteredList: List<Note> = notesList
    private var listener: OnItemClickListener? = null
    
    
    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.note_title)
        val bodyView: TextView = itemView.findViewById(R.id.note_body)
        val bulletPointsView: TextView = itemView.findViewById(R.id.note_bullet_points)
        val dateView: TextView = itemView.findViewById(R.id.note_date)
//        val imageView: ImageView = itemView.findViewById(R.id.note_image)
//        val canvasView: ImageView = itemView.findViewById(R.id.note_canvas)
        val checkbox: RadioButton = itemView.findViewById(R.id.check)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(itemView)
    }
    
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = filteredList[position]
        holder.titleView.text = currentNote.title
        holder.bodyView.text = currentNote.body
        holder.bulletPointsView.text = currentNote.bulletPoints
        holder.dateView.text = currentNote.date
        
//        if (currentNote.regularImagePath.isNotEmpty()) {
//            Glide.with(holder.imageView.context)
//                .load(currentNote.regularImagePath[0])
//                .into(holder.imageView)
//        }

//        if (currentNote.canvasImagePath?.isNotEmpty() == true) {
//            val bitmap = BitmapFactory.decodeByteArray(currentNote.canvasImagePath, 0, currentNote.canvasImagePath!!.size)
//            Glide.with(holder.canvasView.context)
//                .load(bitmap)
//                .into(holder.canvasView)
//        }
        
        
        if (inSelectionMode) {
            holder.checkbox.visibility = View.VISIBLE
        } else {
            holder.checkbox.visibility = View.GONE
        }
    
        if (inSelectionMode) {
            holder.checkbox.isChecked = false
        }
    
        holder.itemView.setOnClickListener {
            if (inSelectionMode) {
                holder.checkbox.isChecked = !holder.checkbox.isChecked
                if (holder.checkbox.isChecked) {
                    selectedItems.add(currentNote)
                } else {
                    selectedItems.remove(currentNote)
                }
            } else {
                listener?.onItemClick(currentNote)
            }
        }
        
        

//        holder.itemView.setOnClickListener {
//            if (!inSelectionMode) {
//                val context = holder.itemView.context
//                val intent = Intent(context, AddNotes::class.java)
//                intent.putExtra("noteId", currentNote.id)
//                context.startActivity(intent)
//            } else {
//                holder.checkbox.isChecked = !holder.checkbox.isChecked
//            }
//        }
    
        holder.itemView.setOnLongClickListener {
            if (!inSelectionMode) {
                toggleSelectionMode(true)
                listener?.onEnterSelectionMode() // Notify the activity/fragment
            }
            true
        }
    
    
    }
    interface OnItemClickListener {
        fun onItemClick(note: Note)
        fun onEnterSelectionMode()
        fun onExitSelectionMode()
    }
    
    
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
    
    @SuppressLint("NotifyDataSetChanged")
    fun toggleSelectionMode(isInSelectionMode: Boolean) {
        this.inSelectionMode = isInSelectionMode
        if (!isInSelectionMode) {
            selectedItems.clear()
            listener?.onExitSelectionMode()
        } else {
            listener?.onEnterSelectionMode()
        }
        notifyDataSetChanged()
    }
    
    
    override fun getItemCount() = filteredList.size
    
    @SuppressLint("NotifyDataSetChanged")
    fun setNotes(notes: List<Note>) {
        this.notesList = notes
        this.filteredList = notes  // Add this line
        notifyDataSetChanged()
    }
    
    fun getSelectedItems(): Set<Note> = selectedItems
    
}
