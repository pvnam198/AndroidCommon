package com.lingvo.base_common.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    // Abstract property for the layout resource ID to be implemented in subclasses
    // Subclasses will provide the layout to be set for the activity
    abstract val layout: Int

    // Option to control whether to enable immersive mode or not
    // By default, immersive mode is disabled (false)
    protected open val enableImmersiveMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content view using the provided layout resource ID from the subclass
        setContentView(layout)

        // Initialize UI components, observers, and listeners specific to this activity
        // These methods can be overridden by subclasses to customize behavior
        setupView()
        setupObservers()
        setupListeners()

        // If immersive mode is enabled, apply it
        if (enableImmersiveMode) {
            enableImmersiveMode()
        }
    }

    /**
     * Initialize and configure UI components.
     * Subclasses can override this method to set up views as needed.
     * For example, finding views by ID, setting up adapters, etc.
     */
    open fun setupView() {}

    /**
     * Set up any observers (e.g., for LiveData) to handle state changes.
     * Subclasses can override this method to set up observers for their data.
     * This is typically where you would observe ViewModels or LiveData objects.
     */
    open fun setupObservers() {}

    /**
     * Set up any click listeners or other interaction handlers.
     * Subclasses can override this method to handle user interactions.
     * For example, setting listeners for buttons, items, etc.
     */
    open fun setupListeners() {}

    /**
     * Clean up resources or stop ongoing tasks before the activity is destroyed.
     * Subclasses can override this method to perform specific cleanup actions, such as cancelling network requests.
     */
    open fun cleanup() {}

    override fun onDestroy() {
        super.onDestroy()
        // Perform any cleanup tasks before the activity is destroyed
        cleanup()
    }

    /**
     * Applies immersive UI mode to hide the status bar and navigation bar.
     * This method creates a full-screen, distraction-free experience by hiding system UI elements.
     * It uses platform-specific APIs to ensure compatibility with different Android versions.
     */
    private fun enableImmersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // For Android Pie (SDK 28) and above, handle the display cutout (notch) area
            val params = window.attributes
            params.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = params
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 (SDK 30) and above, use insets controller to hide system bars
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                // Hide status and navigation bars
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                // Set the system bars to show temporarily on swipe
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // For versions below Android 11, use deprecated flags to hide system bars
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                    )
        }

        // Remove any limits on the window layout to allow for full-screen experience
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    /**
     * Called when the window gains or loses focus.
     * If immersive mode is enabled and the activity gains focus, apply immersive mode.
     * This ensures immersive mode is applied whenever the activity becomes visible.
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && enableImmersiveMode) {
            // Apply immersive mode when the window gains focus
            enableImmersiveMode()
        }
    }
}



