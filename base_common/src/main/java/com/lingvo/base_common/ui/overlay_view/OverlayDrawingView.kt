package com.lingvo.base_common.ui.overlay_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class OverlayDrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint by lazy {
        Paint().apply {
            color = Color.TRANSPARENT
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (width > 0) {
            val w = width
            val h = height
            val interval = w / STEP

            // 斜めパターンの描画
            generateSequence(0f) { it + interval }
                    .take(STEP + h / interval + 1)
                    .forEach { canvas.drawLine(it, 0f, 0f, it, paint) }

            canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        }
    }

    companion object {
        private const val STEP = 9
    }
}