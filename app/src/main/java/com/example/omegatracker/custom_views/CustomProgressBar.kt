package com.example.omegatracker.custom_views

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.omegatracker.R

class CustomProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Attributes
    private var progress = 0f
    private var maxProgress = 100f
    private val strokeWidth = 60f

    // Colors
    private val startColor = ContextCompat.getColor(context, R.color.yoghurt)
    private val endColor = ContextCompat.getColor(context, R.color.darkOrchid)
    private val backgroundColor = ContextCompat.getColor(context, R.color.lavender)

    // Paint objects
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = backgroundColor
        style = Paint.Style.STROKE
        strokeWidth = this@CustomProgressBar.strokeWidth
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = this@CustomProgressBar.strokeWidth
        strokeCap = Paint.Cap.ROUND
    }

    private var gradient: LinearGradient? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Update gradient when size changes
        gradient = LinearGradient(
            0f, 0f, w.toFloat(), h.toFloat(),
            intArrayOf(startColor, endColor),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Calculate center and radius
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = centerX.coerceAtMost(centerY) - strokeWidth / 2

        // Draw background circle
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)

        // Set gradient for progress paint
        progressPaint.shader = gradient

        // Calculate progress angle
        val progressAngle = (progress / maxProgress) * 360

        // Draw progress arc
        canvas.drawArc(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            -90f, // Start from the top
            progressAngle,
            false, // Don't draw the arc's oval
            progressPaint
        )
    }

    // Setters and Getters for Progress
    fun setProgress(progress: Float) {
        this.progress = progress.coerceAtLeast(0f).coerceAtMost(maxProgress)
        invalidate()
    }

    fun getProgress(): Float {
        return progress
    }

    fun setMaxProgress(maxProgress: Float) {
        this.maxProgress = maxProgress
        invalidate()
    }

    fun getMaxProgress(): Float {
        return maxProgress
    }
}