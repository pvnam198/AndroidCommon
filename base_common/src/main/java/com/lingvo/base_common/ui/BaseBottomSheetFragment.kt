package com.lingvo.base_common.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetFragment : BottomSheetDialogFragment() {

    // Abstract property for layout resource ID
    abstract val layout: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this bottom sheet
        return inflater.inflate(layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI components, observers, and listeners
        setupView(view)
        setupObservers()
        setupListeners()
    }

    /**
     * Initialize and configure UI components.
     * Subclasses can override for specific setup.
     * @param view The root view of the bottom sheet.
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
}
