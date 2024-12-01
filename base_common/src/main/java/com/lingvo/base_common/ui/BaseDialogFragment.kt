package com.lingvo.base_common.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

abstract class BaseDialogFragment : DialogFragment() {

    // Abstract property for layout resource ID
    abstract val layout: Int

    // Flag to control whether the dialog should be full-screen
    open val enableFullScreenMode: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this dialog
        return inflater.inflate(layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adjust the dialog's window size if full-screen is enabled
        if (enableFullScreenMode) {
            val dialogWindow = dialog?.window
            dialogWindow?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Initialize UI components, observers, and listeners
        setupView(view)
        setupObservers()
        setupListeners()
    }

    /**
     * Initialize and configure UI components.
     * Subclasses can override for specific setup.
     * @param view The root view of the dialog.
     */
    open fun setupView(view: View) {}

    /**
     * Observe LiveData or other reactive components.
     * Subclasses can override to handle state changes.
     */
    open fun setupObservers() {}

    /**
     * Set up click listeners or other interaction handlers.
     * Subclasses can override for specific interactions.
     */
    open fun setupListeners() {}

    /**
     * Handle cleanup of resources or listeners.
     * Subclasses can override for specific cleanup actions.
     */
    open fun cleanup() {}

    override fun onDestroyView() {
        super.onDestroyView()
        // Perform cleanup actions defined in the cleanup method
        cleanup()
    }

    override fun show(fragmentManager: FragmentManager, tag: String?) {
        if (fragmentManager.findFragmentByTag(tag) != null) return
        super.show(fragmentManager, tag)
    }


}

