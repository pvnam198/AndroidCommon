package com.lingvo.base_common.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    // Abstract property for layout resource ID
    abstract val layout: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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
     * @param view The root view of the fragment.
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
     * Clean up resources or stop tasks.
     * Subclasses can override to handle specific cleanup actions.
     */
    open fun cleanup() {}

    override fun onDestroyView() {
        super.onDestroyView()
        // Perform cleanup actions defined in the cleanup method
        cleanup()
    }
}
