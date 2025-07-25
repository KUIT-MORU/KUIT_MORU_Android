// TutorialOverlayView.kt
package com.konkuk.moru.presentation.home.screen

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class TutorialOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var holes: List<HolePx> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    private val overlayPaint = Paint().apply {
        color = Color.parseColor("#CC000000")
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val saved = canvas.saveLayer(null, null) // 중요: 레이어 생성
        //오버레이를 검은색으로 칠하기
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)

        holes.forEach { hole ->
            if (hole.isCircle) {
                canvas.drawCircle(hole.centerX, hole.centerY, hole.radius, clearPaint)
            } else {
                canvas.drawRoundRect(
                    hole.left, hole.top, hole.right, hole.bottom,
                    hole.cornerRadius, hole.cornerRadius,
                    clearPaint
                )
            }
        }

        canvas.restoreToCount(saved)
    }

    data class HolePx(
        val left: Float,
        val top: Float,
        val right: Float,
        val bottom: Float,
        val cornerRadius: Float = 0f,
        val isCircle: Boolean = false
    ) {
        val centerX: Float get() = (left + right) / 2
        val centerY: Float get() = (top + bottom) / 2
        val radius: Float get() = (right - left) / 2
    }
}

