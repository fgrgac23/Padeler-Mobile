package com.example.rampu2506_padeler.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.rampu2506_padeler.R
import com.example.rampu2506_padeler.entities.User
import com.example.rampu2506_padeler.repositories.RepoProvider
import kotlinx.coroutines.launch

class RegisterFragment3 : Fragment() {

    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.activity_register_3, container, false)

        val btnFinish = view.findViewById<Button>(R.id.btnARegFinish)
        val spnFrequency = view.findViewById<Spinner>(R.id.spnARegFrequency)
        val spnLevel = view.findViewById<Spinner>(R.id.spnARegLevel)
        val spnPosition = view.findViewById<Spinner>(R.id.spnARegPosition)

        btnFinish.isEnabled = false
        btnFinish.alpha = 0.5f

        fun checkAllSelected() {
            val freq = spnFrequency.selectedItemPosition != 0
            val level = spnLevel.selectedItemPosition != 0
            val pos = spnPosition.selectedItemPosition != 0

            btnFinish.isEnabled = freq && level && pos
            btnFinish.alpha = if (btnFinish.isEnabled) 1f else 0.5f
        }

        fun setupSpinner(spinner: Spinner, arrayRes: Int) {
            val adapter = ArrayAdapter.createFromResource(
                requireContext(),
                arrayRes,
                R.layout.spinner_item_white
            ).apply {
                setDropDownViewResource(R.layout.spinner_item_white)
            }

            spinner.adapter = adapter
            spinner.setSelection(0, false)

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                    checkAllSelected()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        setupSpinner(spnFrequency, R.array.frequency_options)
        setupSpinner(spnLevel, R.array.level_options)
        setupSpinner(spnPosition, R.array.position_options)

        btnFinish.setOnClickListener {
            viewModel.frequencyOfPlay = spnFrequency.selectedItem.toString()
            viewModel.levelOfPlay = spnLevel.selectedItem.toString()
            viewModel.position = spnPosition.selectedItem.toString()

            if (viewModel.dateOfBirth == null) {
                Toast.makeText(requireContext(), "Date of birth is missing!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uri = viewModel.imageUri
            var imgBytes: ByteArray? = null
            var imgMime: String? = null

            if (uri != null) {
                val cr = requireContext().contentResolver
                imgMime = cr.getType(uri)
                imgBytes = cr.openInputStream(uri)?.use { it.readBytes() }
            }


            val tmpUser = User(
                userId = 0,
                username = viewModel.username.trim(),
                email = viewModel.email.trim(),
                passwordHash = viewModel.password.trim(),
                phone = viewModel.phoneNumber.trim().ifBlank { null },
                name = viewModel.name.trim().ifBlank { null },
                surname = viewModel.surname.trim().ifBlank { null },
                gender = viewModel.gender,
                dateOfBirth = viewModel.dateOfBirth,
                frequencyOfPlay = viewModel.frequencyOfPlay,
                levelOfPlay = viewModel.levelOfPlay,
                position = viewModel.position,
                rating = null,
                numberOfRatings = null,
                numOfSwipes = null,
                latitude = viewModel.geoLat,
                longitude = viewModel.geoLong,
                blocked = false,
                image = imgBytes,
                mimeType = imgMime,
                distance = null,
                )

            btnFinish.isEnabled = false
            btnFinish.alpha = 0.5f

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val created = RepoProvider.users.register(tmpUser, viewModel.password)
                    if (created == null) {
                        btnFinish.isEnabled = true
                        btnFinish.alpha = 1f
                        Toast.makeText(requireContext(), "Registration failed (user/email may already exist).", Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    val prefs = requireActivity().getSharedPreferences("user_prefs", 0)
                    prefs.edit().putInt("logged_user_id", created.userId).apply()

                    Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out,
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                        )
                        .replace(R.id.mainLayout, LoginFragment())
                        .addToBackStack(null)
                        .commit()
                } catch (e: Exception) {
                    btnFinish.isEnabled = true
                    btnFinish.alpha = 1f
                    Toast.makeText(requireContext(), "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        return view
    }
}
