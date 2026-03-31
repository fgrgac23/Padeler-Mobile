package com.example.rampu2506_padeler.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.rampu2506_padeler.R
import com.google.android.material.card.MaterialCardView
import androidx.core.os.bundleOf
import android.widget.ArrayAdapter
import android.widget.Spinner


class FilterFragment : Fragment(R.layout.fragment_filter) {

    companion object {
        private const val PREFS = "padeler_prefs"
        private const val KEY_RADIUS_KM = "radius_km"

        private const val KEY_GENDER = "gender"
        private const val KEY_LEVEL = "level"
        private const val KEY_POSITION = "position"
        private const val KEY_FREQUENCY = "frequency"

        private const val DEFAULT_RADIUS = 10
        private const val MIN_RADIUS = 1
        private const val MAX_RADIUS = 50
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvRadiusValue = view.findViewById<TextView>(R.id.tvRadiusValue)
        val seekRadius = view.findViewById<SeekBar>(R.id.seekRadius)

        val spGender = view.findViewById<Spinner>(R.id.spGender)
        val spLevel = view.findViewById<Spinner>(R.id.spLevel)
        val spPosition = view.findViewById<Spinner>(R.id.spPosition)
        val spFrequency = view.findViewById<Spinner>(R.id.spFrequency)


        val cardApply = view.findViewById<MaterialCardView>(R.id.cardApply)
        val btnApply = view.findViewById<TextView>(R.id.btnApply)

        val prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        seekRadius.max = (MAX_RADIUS - MIN_RADIUS)

        val savedRadius = prefs.getInt(KEY_RADIUS_KM, DEFAULT_RADIUS).coerceIn(MIN_RADIUS, MAX_RADIUS)
        seekRadius.progress = savedRadius - MIN_RADIUS

        tvRadiusValue.text = getString(R.string.radius_value, savedRadius)

        seekRadius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val radius = progress + MIN_RADIUS
                tvRadiusValue.text = getString(R.string.radius_value, radius)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        fun setupFilterSpinner(spinner: Spinner, arrayRes: Int, savedValue: String?) {
            val base = resources.getStringArray(arrayRes).toList()
            val cleaned = if (base.isNotEmpty() && base[0].contains("Select", ignoreCase = true)) {
                base.drop(1)
            } else base

            val items = listOf("Any") + cleaned

            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item_white,
                items
            ).apply {
                setDropDownViewResource(R.layout.spinner_item_white)
            }
            spinner.adapter = adapter
            val idx = if (savedValue.isNullOrBlank()) 0 else items.indexOf(savedValue).let { if (it >= 0) it else 0 }
            spinner.setSelection(idx, false)
        }

        val savedGender = prefs.getString(KEY_GENDER, "") ?: ""
        val savedLevel = prefs.getString(KEY_LEVEL, "") ?: ""
        val savedPosition = prefs.getString(KEY_POSITION, "") ?: ""
        val savedFrequency = prefs.getString(KEY_FREQUENCY, "") ?: ""

        setupFilterSpinner(spGender, R.array.gender_options, savedGender)
        setupFilterSpinner(spLevel, R.array.level_options, savedLevel)
        setupFilterSpinner(spPosition, R.array.position_options, savedPosition)
        setupFilterSpinner(spFrequency, R.array.frequency_options, savedFrequency)

        val applyAction = View.OnClickListener {
            val radius = seekRadius.progress + MIN_RADIUS

            fun selectedOrEmpty(sp: Spinner): String {
                val s = sp.selectedItem?.toString()?.trim().orEmpty()
                return if (s.equals("Any", ignoreCase = true)) "" else s
            }

            val gender = selectedOrEmpty(spGender)
            val level = selectedOrEmpty(spLevel)
            val position = selectedOrEmpty(spPosition)
            val frequency = selectedOrEmpty(spFrequency)

            prefs.edit()
                .putInt(KEY_RADIUS_KM, radius)
                .putString(KEY_GENDER, gender)
                .putString(KEY_LEVEL, level)
                .putString(KEY_POSITION, position)
                .putString(KEY_FREQUENCY, frequency)
                .apply()

            parentFragmentManager.setFragmentResult(
                "filters_applied",
                bundleOf("radius_km" to radius)
            )

            parentFragmentManager.popBackStack()
        }

        cardApply.setOnClickListener(applyAction)
        btnApply.setOnClickListener(applyAction)
    }
}
