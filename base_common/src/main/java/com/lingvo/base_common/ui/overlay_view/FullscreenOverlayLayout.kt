package com.lingvo.base_common.ui.overlay_view

import android.content.Context
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.os.Build
import android.util.AttributeSet
import android.view.Display
import android.view.WindowManager
import android.view.WindowMetrics
import android.widget.FrameLayout

// FullscreenOverlayLayout is a custom FrameLayout used to create a full-screen overlay
// that adjusts itself based on the screen size, ensuring it works well across different Android versions.
// For more details and usage, check the following link:
// https://gist.github.com/pvnam198/18ffb9254a11a00a33617b2df24cfe48

class FullscreenOverlayLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // Preallocate Point to reuse during measurements
    private val screenSize = Point()

    // Override onMeasure to adjust the layout size according to the screen size
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 (API 30) and above, use DisplayManager to get the default display
            val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            displayManager.getDisplay(Display.DEFAULT_DISPLAY)
        } else {
            // For older versions, fallback to the default window manager
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For Android 12 (API 31) and above, use WindowMetrics to get screen size
            try {
                val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
                screenSize.set(windowMetrics.bounds.width(), windowMetrics.bounds.height())
            } catch (e: NoSuchMethodError) {
                // Handle gracefully if API 30 or above is not available
                @Suppress("DEPRECATION")
                display.getRealSize(screenSize)
            }
        } else {
            // For API 29 (Android 10) and below, use getRealSize()
            @Suppress("DEPRECATION")
            display.getRealSize(screenSize)
        }

        // Call the superclass method to measure the layout using the calculated screen size
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(screenSize.x, MeasureSpec.getMode(widthMeasureSpec)),
            MeasureSpec.makeMeasureSpec(screenSize.y, MeasureSpec.getMode(heightMeasureSpec))
        )
    }
}