@file:Suppress("DEPRECATION")

package com.example.taskandnotes.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.taskandnotes.R
import com.example.taskandnotes.adapters.CanvasImageAdapter
import com.example.taskandnotes.adapters.ImageAdapter
import com.example.taskandnotes.data.viewModels.NoteViewModel
import com.example.taskandnotes.databinding.ActivityAddnoteBinding
import com.example.taskandnotes.db.Note
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddNotes : AppCompatActivity() {
    private lateinit var binding: ActivityAddnoteBinding
    private lateinit var viewModel: NoteViewModel
    private var PICK_IMAGES = 1
    private var selectedImagePaths = mutableListOf<String>()
    private val selectedImagePathsLiveData: MutableLiveData<MutableList<String>> = MutableLiveData<MutableList<String>>()
    
    private val imageAdapter = ImageAdapter(this, selectedImagePaths)
    private val REQUEST_PERMISSION_CODE = 1001
    
    
    private var canvasImagePaths = mutableListOf<ByteArray>()
    private val canvasImageAdapter = CanvasImageAdapter(canvasImagePaths)
    private val canvasImagePathLiveData: MutableLiveData<MutableList<ByteArray>> = MutableLiveData<MutableList<ByteArray>>()
    
    
    private val REQUEST_CAMERA_PERMISSION_CODE = 1002
    private val CAPTURE_IMAGE_REQUEST = 2
    val YOUR_CANVAS_ACTIVITY_REQUEST_CODE= 1003

    private var photoURI: Uri? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddnoteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    
        if(intent.getBooleanExtra("SHOW_ADD_IMAGE_DIALOG", false)) {
            showImageSourceDialog()
        }
    
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]
    
       // val imageAdapter = ImageAdapter(this, selectedImagePaths)
        binding.imageRecycler.adapter = this.imageAdapter
        
        
        selectedImagePathsLiveData.observe(this) { updatedList ->
            selectedImagePaths.clear()
            selectedImagePaths.addAll(updatedList)
            imageAdapter.notifyDataSetChanged()
        }
    
        binding.canvasImageRecycler.adapter = canvasImageAdapter
    
        canvasImagePathLiveData.observe(this) { updatedList ->
            canvasImagePaths.clear()
            canvasImagePaths.addAll(updatedList)
            canvasImageAdapter.notifyDataSetChanged()
        }
        
    
    
        binding.back.setOnClickListener{
            onBackPressed()
        }
    
        binding.bulletPointsEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty() && s.last() == '\n') {
                    s.append("• ")
                } else if (s.length == 1 && s[0] != '•') {  // If the user deletes the first bullet point
                    s.insert(0, "• ")
                } else if (s.isEmpty()) {
                    resetViewVisibility()
                }
            }
        
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    
    
    
        val noteId = intent.getLongExtra("NOTE_ID", -1L)
        if(noteId != -1L) {
            viewModel.getNoteById(noteId).observe(this, Observer { note ->
                if(note != null) {
                    populateUI(note)
                }
            })
        }
    
    
    
    
        binding.popupMenu.setOnClickListener{
            val popupMenu = PopupMenu(this, it)
            popupMenu.inflate(R.menu.menu1)
    
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu1 -> {
                        showImageSourceDialog()
                        true
                    }
            
                    R.id.menu2 -> {
                        val intent = Intent(this, CanvasActivity::class.java)
                        startActivityForResult(intent, YOUR_CANVAS_ACTIVITY_REQUEST_CODE)
    
                        true
                    }
            
                    R.id.menu3 -> {
                        toggleBulletPointsVisibility()
                
                        true
                    }
                    
//                    R.id.menu4 -> {
//
//                        true
//                    }
            
                    else -> false
                }
            }
    
            popupMenu.show()
        }
    
        binding.done.setOnClickListener {
            val title = binding.titleNote.text.toString().trim()
            val content = binding.bodyNote.text.toString().trim()
            val bulletPoints = binding.bulletPointsEdittext.text.toString().trim()
            val date = getCurrentDateTime()
        
            if (title.isNotEmpty() || bulletPoints.isNotEmpty()) {
                val note = Note(
                    id = if (noteId != -1L) noteId else 0,
                    title = title,
                    body = content,
                    bulletPoints = bulletPoints,
                    date = date,
                    regularImagePath = selectedImagePaths
                )
            
                if (noteId != -1L) {
                    viewModel.update(note)
                } else {
                    viewModel.insert(note)
                }
            
                finish()
            } else {
                Toast.makeText(this, "Add Title or Bullet Points", Toast.LENGTH_SHORT).show()
            }
        }
        
    
    }
    
    private fun populateUI(note: Note) {
        binding.titleNote.setText(note.title)
        binding.bodyNote.setText(note.body)
        
        if (note.bulletPoints.isNotEmpty()) {
            binding.bulletPointsEdittext.setText(note.bulletPoints)
            binding.bulletPointsEdittext.visibility = View.VISIBLE
            binding.bodyNote.visibility = View.INVISIBLE // Assuming you want to hide the body when showing bullet points
            binding.titleNote.visibility = View.VISIBLE
            binding.imageRecycler.visibility = View.INVISIBLE
            binding.canvasImageRecycler.visibility = View.INVISIBLE
        } else {
            resetViewVisibility()
        }
        
        if (note.regularImagePath.isNotEmpty()) {
            val imagePaths = note.regularImagePath
            selectedImagePaths.clear()
            selectedImagePaths.addAll(imagePaths)
            imageAdapter.notifyDataSetChanged()
        }
        
        note.canvasImagePath?.let {
            canvasImagePaths.add(it)
            canvasImageAdapter.notifyDataSetChanged()
        }
    }
    
    private fun toggleBulletPointsVisibility() {
        if (binding.bulletPointsEdittext.visibility == View.INVISIBLE) {
            binding.bulletPointsEdittext.visibility = View.VISIBLE
            binding.titleNote.visibility= View.VISIBLE
            binding.bodyNote.visibility = View.INVISIBLE
            binding.imageRecycler.visibility = View.INVISIBLE
            binding.canvasImageRecycler.visibility = View.INVISIBLE
            
            binding.bulletPointsEdittext.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.bulletPointsEdittext, InputMethodManager.SHOW_IMPLICIT)
            
        } else {
            binding.bulletPointsEdittext.visibility = View.INVISIBLE
            binding.bodyNote.visibility = View.VISIBLE
            binding.titleNote.visibility = View.VISIBLE
            binding.imageRecycler.visibility = View.VISIBLE
            binding.canvasImageRecycler.visibility = View.VISIBLE
        }
    }
    
    
    private fun resetViewVisibility() {
        binding.bulletPointsEdittext.visibility = View.INVISIBLE
        binding.bodyNote.visibility = View.VISIBLE
        binding.titleNote.visibility = View.VISIBLE
        binding.imageRecycler.visibility = View.VISIBLE
        binding.canvasImageRecycler.visibility = View.VISIBLE
    }
    
    
    
    private fun showImageSourceDialog() {
        val options = arrayOf("Choose from Gallery", "Take Photo")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Select Image From:")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> checkPermissionAndPickImage()
                1 -> checkCameraPermissionAndCaptureImage()
            }
        }
        builder.show()
    }
    
    private fun checkCameraPermissionAndCaptureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION_CODE
            )
        } else {
            captureImage()
        }
    }
    
    private fun captureImage() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
                null
            }
            photoFile?.also {
                photoURI = FileProvider.getUriForFile(
                    this,
                    "com.example.taskandnotes.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)
            }
        }
    }
    
    
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }
    
    
    
    private fun checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            
            
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE
            )
        } else {
            
            pickImage()
        }
    }
    
    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, PICK_IMAGES)
    }
    
    
    
    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
    
    
    
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        val tempList = mutableListOf<String>()
        tempList.addAll(selectedImagePaths)
    
        
        when (requestCode) {
            PICK_IMAGES -> {
                if (resultCode == RESULT_OK) {
                    if (data?.clipData != null) {
                        val clipData = data.clipData!!
                        for (i in 0 until clipData.itemCount) {
                            val imageUri = clipData.getItemAt(i).uri
                            val imagePath = getPathFromURI(imageUri)
                            tempList.add(imagePath)
                        }
                    } else if (data?.data != null) {
                        val imageUri = data.data!!
                        val imagePath = getPathFromURI(imageUri)
                        tempList.add(imagePath)
                    }
                }
            }
    
            CAPTURE_IMAGE_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    photoURI?.let {
                        val imageFile = File(it.path!!)
                        if (imageFile.exists()) {
                            tempList.add(imageFile.absolutePath)
                        }
                    }
                }
            }
            YOUR_CANVAS_ACTIVITY_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    val returnedImageByteArray = data?.getByteArrayExtra("SAVED_CANVAS_IMAGE")
                    if (returnedImageByteArray != null) {
                        canvasImageAdapter.addBitmap(returnedImageByteArray)
                    }
                }
            }
    
    
        }
        
        selectedImagePathsLiveData.value = tempList
    }
    
    
    private fun getPathFromURI(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val idx = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        val imagePath = cursor?.getString(idx!!)
        cursor?.close()
        
        val file = File(imagePath ?: "")
        if (file.exists()) {
            return imagePath ?: ""
        } else {
            Toast.makeText(this, "File doesn't exist!", Toast.LENGTH_SHORT).show()
            return ""
        }
    }
    
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, pick the image
                    pickImage()
                } else {
                    // Permission denied
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, capture image
                    captureImage()
                } else {
                    // Permission denied
                    Toast.makeText(this, "Camera Permission denied!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    
}

