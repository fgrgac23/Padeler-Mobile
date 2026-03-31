package com.example.rampu2506_padeler.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import com.example.rampu2506_padeler.R
import androidx.lifecycle.lifecycleScope
import com.example.rampu2506_padeler.entities.User
import com.example.rampu2506_padeler.repositories.RepoProvider
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)

class EditProfileFragment : Fragment() {
    lateinit var name : EditText
    lateinit var surname : EditText
    lateinit var phoneNumber : EditText
    lateinit var date : EditText
    lateinit var username : EditText
    lateinit var password : EditText
    lateinit var email : EditText
    lateinit var gender : Spinner
    lateinit var frequency : Spinner
    lateinit var level : Spinner
    lateinit var position : Spinner
    lateinit var image : ImageView
    private val selectedDateTime : Calendar = Calendar.getInstance()

    private var selectedImageBytes: ByteArray? = null
    private var selectedMimeType: String? = null

    private val pickImage = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@registerForActivityResult

        val cr = requireContext().contentResolver
        selectedMimeType = cr.getType(uri)

        val bytes = cr.openInputStream(uri)?.use { it.readBytes() }
        if (bytes == null) return@registerForActivityResult

        if (bytes.size > 600_000) {
            Toast.makeText(requireContext(), "Picture is too big (max 600 KB)", Toast.LENGTH_SHORT).show()
            selectedImageBytes = null
            selectedMimeType = null
            return@registerForActivityResult
        }

        selectedImageBytes = bytes
        image.setImageBitmap(
            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val prefs = requireActivity().getSharedPreferences("user_prefs", 0)
        val loggedUserId = prefs.getInt("logged_user_id", 0)
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        val saveButton = view.findViewById<Button>(R.id.btnEPSave)

        name = view.findViewById(R.id.etEPName)
        surname = view.findViewById(R.id.etEPSurname)
        phoneNumber = view.findViewById(R.id.etEPPhone)
        date = view.findViewById(R.id.etEPDateOfBirth)
        username = view.findViewById(R.id.etEPUsername)
        password = view.findViewById(R.id.etEPPassword)
        email = view.findViewById(R.id.etEPEmail)
        gender = view.findViewById(R.id.spnEPGender)
        frequency = view.findViewById(R.id.spnEPFrequency)
        level = view.findViewById(R.id.spnEPLevel)
        position = view.findViewById(R.id.spnEPPosition)
        password.setText("")
        password.isEnabled = false
        image = view.findViewById(R.id.imgEPProfile)

        var currentUser: User? = null

        viewLifecycleOwner.lifecycleScope.launch {
            RepoProvider.users.refreshUser(loggedUserId)
            currentUser = RepoProvider.users.getByIdLocal(loggedUserId)
            val user = currentUser
            if (user == null) {
                Toast.makeText(requireContext(), "Profile couldn't be loaded.", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
                return@launch
            }

            name.setText(user.name ?: "")
            surname.setText(user.surname ?: "")
            phoneNumber.setText(user.phone ?: "")
            date.setText(user.dateOfBirth?.let { dateFormat.format(it) } ?: "")
            username.setText(user.username)
            email.setText(user.email)

            val (bytes, mime) = RepoProvider.users.fetchUserImage(loggedUserId)
            if (bytes != null && mime != null) {
                selectedImageBytes = bytes
                selectedMimeType = mime
                val bmp = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                image.setImageBitmap(bmp)
            } else {
                image.setImageResource(R.drawable.person)
            }

        }

        image.setOnClickListener {
            pickImage.launch(arrayOf("image/*"))
        }


        val genderAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_options,
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        gender.adapter = genderAdapter

        val frequencyAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.frequency_options,
            android.R.layout.simple_spinner_item
        )
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        frequency.adapter = frequencyAdapter

        val levelAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.level_options,
            android.R.layout.simple_spinner_item
        )
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        level.adapter = levelAdapter

        val positionAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.position_options,
            android.R.layout.simple_spinner_item
        )
        positionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        position.adapter = positionAdapter

        activateDateTimeListeners()

        viewLifecycleOwner.lifecycleScope.launch {
            val user = RepoProvider.users.getByIdLocal(loggedUserId)
            if (user != null) {
                user.gender?.let { setSpinnerSelection(gender, it) }
                user.frequencyOfPlay?.let { setSpinnerSelection(frequency, it) }
                user.levelOfPlay?.let { setSpinnerSelection(level, it) }
                user.position?.let { setSpinnerSelection(position, it) }
            }
        }

        saveButton.setOnClickListener {
            if (
                name.text.isBlank() ||
                surname.text.isBlank() ||
                phoneNumber.text.isBlank() ||
                date.text.isBlank() ||
                username.text.isBlank() ||
                email.text.isBlank() ||
                gender.selectedItemPosition == 0 ||
                frequency.selectedItemPosition == 0 ||
                level.selectedItemPosition == 0 ||
                position.selectedItemPosition == 0
            ) {
                Toast.makeText(context, "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }else{
                val base = currentUser
                if (base == null) {
                    Toast.makeText(context, "Profile couldn't be saved.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val parsedDob = dateFormat.parse(date.text.toString())
                if (parsedDob == null) {
                    Toast.makeText(context, "Invalid date.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val updatedUser = base.copy(
                    name = name.text.toString().trim(),
                    surname = surname.text.toString().trim(),
                    username = username.text.toString().trim(),
                    phone = phoneNumber.text.toString().trim(),
                    dateOfBirth = parsedDob,
                    email = email.text.toString().trim(),
                    gender = gender.selectedItem.toString(),
                    frequencyOfPlay = frequency.selectedItem.toString(),
                    levelOfPlay = level.selectedItem.toString(),
                    position = position.selectedItem.toString(),
                    image = selectedImageBytes ?: base.image,
                    mimeType = selectedMimeType ?: base.mimeType
                )

                viewLifecycleOwner.lifecycleScope.launch {
                    val result = RepoProvider.users.updateProfile(updatedUser)
                    if (result == null) {
                        Toast.makeText(context, "Error while saving profile!", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    Toast.makeText(context, "Changes saved successfully!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.setFragmentResult("profile_updated", Bundle())
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        return view
    }
    fun activateDateTimeListeners() {
        val listener = View.OnClickListener {
            val dialog = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    selectedDateTime.set(year, month, day)
                    date.setText(dateFormat.format(selectedDateTime.time))
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.maxDate = System.currentTimeMillis()
            dialog.show()
        }
        date.setOnClickListener(listener)
        date.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) listener.onClick(date)
        }
        date.keyListener = null
    }
    fun setSpinnerSelection(spinner: Spinner, value: String) {
        val target = value.trim()
        val adapter = spinner.adapter
        for (i in 0 until adapter.count) {
            val item = adapter.getItem(i).toString().trim()
            if (item.equals(target, ignoreCase = true)) {
                spinner.setSelection(i)
                break
            }
        }
    }
}