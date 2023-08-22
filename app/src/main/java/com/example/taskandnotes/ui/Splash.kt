package com.example.taskandnotes.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.taskandnotes.databinding.ActivitySplashBinding

class Splash : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        
        binding.continueButton.visibility = View.INVISIBLE
        binding.progress.visibility = View.VISIBLE
        
        Handler(Looper.getMainLooper()).postDelayed({
            binding.progress.visibility = View.GONE
            binding.continueButton.visibility = View.VISIBLE
            
            binding.continueButton.setOnClickListener {
                val intent = Intent(this@Splash, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 4000)
    }
}
