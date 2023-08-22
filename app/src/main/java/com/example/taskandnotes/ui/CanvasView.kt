package com.example.taskandnotes.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CustomCanvasView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var drawPath: Path = Path()
    private var canvasBitmap: Bitmap? = null
    private var drawCanvas: Canvas? = null
    private var currentPaint: Paint = Paint()
    private val paths = ArrayList<DrawPath>()
    private val undonePaths = ArrayList<DrawPath>()
    
    init {
        currentPaint.color = Color.BLUE
        currentPaint.isAntiAlias = true
        currentPaint.strokeWidth = 5f
        currentPaint.style = Paint.Style.STROKE
        currentPaint.strokeJoin = Paint.Join.ROUND
        currentPaint.strokeCap = Paint.Cap.ROUND
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap!!)
    }
    
    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(canvasBitmap!!, 0f, 0f, currentPaint)
        canvas.drawPath(drawPath, currentPaint)
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y
        
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                drawPath.moveTo(touchX, touchY)
            }
            MotionEvent.ACTION_MOVE -> {
                drawPath.lineTo(touchX, touchY)
            }
            MotionEvent.ACTION_UP -> {
                drawCanvas?.drawPath(drawPath, currentPaint)
                val pathCopy = Path(drawPath)
                paths.add(DrawPath(pathCopy, copyPaint(currentPaint)))
                undonePaths.clear()
                drawPath.reset()
            }
        }
        
        invalidate()
        return true
    }
    
    fun undo() {
        if (paths.isNotEmpty()) {
            val lastPathIndex = paths.size - 1
            val removedPath = paths.removeAt(lastPathIndex)
            undonePaths.add(removedPath)
            redrawCanvas()
        }
    }
    fun redo() {
        if (undonePaths.isNotEmpty()) {
            val lastUndonePathIndex = undonePaths.size - 1
            val restoredPath = undonePaths.removeAt(lastUndonePathIndex)
            paths.add(restoredPath)
            redrawCanvas()
        }
    }
    
    private fun redrawCanvas() {
        canvasBitmap?.eraseColor(Color.TRANSPARENT)
        for (path in paths) {
            drawCanvas?.drawPath(path.path, path.paint)
        }
        invalidate()
    }
    
    fun getBitmap(): Bitmap {
        return canvasBitmap ?: Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }
    
    fun setCurrentColor(color: Int) {
        currentPaint.color = color
    }
    
    private fun copyPaint(original: Paint): Paint {
        val copy = Paint()
        copy.color = original.color
        copy.isAntiAlias = original.isAntiAlias
        copy.strokeWidth = original.strokeWidth
        copy.style = original.style
        copy.strokeJoin = original.strokeJoin
        copy.strokeCap = original.strokeCap
        copy.shader = original.shader
        return copy
    }
    
    internal inner class DrawPath(var path: Path, var paint: Paint)
}
