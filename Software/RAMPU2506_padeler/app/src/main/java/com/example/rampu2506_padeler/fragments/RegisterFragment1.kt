package com.example.rampu2506_padeler.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.rampu2506_padeler.R
import java.text.SimpleDateFormat
import java.util.*
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.ImageView


class RegisterFragment1 : Fragment() {

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                viewModel.imageUri = uri
                view?.findViewById<ImageView>(R.id.imgARegProfile)?.setImageURI(uri)
            }
        }

    private val viewModel: RegisterViewModel by activityViewModels()
    private var selectedDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_register_1, container, false)

        val register = view.findViewById<Button>(R.id.btnARegNext1)
        val genderSpinner = view.findViewById<Spinner>(R.id.spnARegGender)
        val etDateOfBirth = view.findViewById<EditText>(R.id.etARegDateOfBirth)
        val etName = view.findViewById<EditText>(R.id.etARegName)
        val etSurname = view.findViewById<EditText>(R.id.etARegSurname)

        val iv = view.findViewById<ImageView>(R.id.imgARegProfile)
        viewModel.imageUri?.let { iv.setImageURI(it) }
        iv.setOnClickListener {
            pickImage.launch("image/*")
        }

        register.isEnabled = false
        register.alpha = 0.5f

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_options,
            R.layout.spinner_item_white
        ).apply {
            setDropDownViewResource(R.layout.spinner_item_white)
        }

        genderSpinner.adapter = adapter
        genderSpinner.setSelection(0, false)

        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        etDateOfBirth.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val datePicker = DatePickerDialog(
                    requireContext(),
                    { _, y, m, d ->
                        val picked = Calendar.getInstance()
                        picked.set(y, m, d)

                        if (picked.after(Calendar.getInstance())) {
                            Toast.makeText(requireContext(), "Invalid date!", Toast.LENGTH_SHORT).show()
                            return@DatePickerDialog
                        }

                        selectedDate = picked.time
                        etDateOfBirth.setText(dateFormatter.format(picked.time))
                        validateForm(etName, etSurname, genderSpinner, etDateOfBirth, register)
                    },
                    year, month, day
                )

                datePicker.datePicker.maxDate = System.currentTimeMillis()
                datePicker.show()
            }
        }

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateForm(etName, etSurname, genderSpinner, etDateOfBirth, register)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etName.addTextChangedListener(textWatcher)
        etSurname.addTextChangedListener(textWatcher)

        genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                validateForm(etName, etSurname, genderSpinner, etDateOfBirth, register)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        register.setOnClickListener {

            viewModel.name = etName.text.toString()
            viewModel.surname = etSurname.text.toString()
            viewModel.gender = genderSpinner.selectedItem.toString()
            viewModel.dateOfBirth = selectedDate

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
                .replace(R.id.mainLayout, RegisterFragment2())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun validateForm(
        etName: EditText,
        etSurname: EditText,
        genderSpinner: Spinner,
        etDateOfBirth: EditText,
        register: Button
    ) {
        val isNameValid = etName.text.toString().trim().isNotEmpty()
        val isSurnameValid = etSurname.text.toString().trim().isNotEmpty()
        val isGenderValid = genderSpinner.selectedItemPosition != 0
        val isDateValid = selectedDate != null

        etName.error = if (!isNameValid) "Please enter your name" else null
        etSurname.error = if (!isSurnameValid) "Please enter your surname" else null
        etDateOfBirth.error = if (!isDateValid) "Please select your date of birth" else null

        val isFormValid = isNameValid && isSurnameValid && isGenderValid && isDateValid

        register.isEnabled = isFormValid
        register.alpha = if (isFormValid) 1f else 0.5f
    }
}
