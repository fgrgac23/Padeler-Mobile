package com.example.rampu2506_padeler.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.rampu2506_padeler.R

class RegisterFragment2 : Fragment() {

    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_register_2, container, false)

        val btnNext = view.findViewById<Button>(R.id.btnARegNext2)
        val etUsername = view.findViewById<EditText>(R.id.etARegUsername)
        val etEmail = view.findViewById<EditText>(R.id.etARegEmail)
        val etPassword = view.findViewById<EditText>(R.id.etARegPassword)
        val etPhone = view.findViewById<EditText>(R.id.etARegPhoneNumber)

        btnNext.isEnabled = false
        btnNext.alpha = 0.5f

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateForm(etUsername, etEmail, etPassword, etPhone, btnNext)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etUsername.addTextChangedListener(watcher)
        etEmail.addTextChangedListener(watcher)
        etPassword.addTextChangedListener(watcher)
        etPhone.addTextChangedListener(watcher)

        btnNext.setOnClickListener {
            viewModel.username = etUsername.text.toString().trim()
            viewModel.email = etEmail.text.toString().trim()
            viewModel.password = etPassword.text.toString().trim()
            viewModel.phoneNumber = etPhone.text.toString().trim()

            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
                .replace(R.id.mainLayout, RegisterFragment3())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun validateForm(
        etUsername: EditText,
        etEmail: EditText,
        etPassword: EditText,
        etPhone: EditText,
        btnNext: Button
    ) {
        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val phone = etPhone.text.toString().trim()

        val isUsernameValid = username.isNotEmpty()
        val isEmailFormatValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = isPasswordStrong(password)

        val phoneRegex = Regex("^\\+?[0-9]{6,15}$")
        val isPhoneFormatValid = phoneRegex.matches(phone)

        etEmail.error = if (email.isNotEmpty() && !isEmailFormatValid) "Invalid email format" else null

        etPassword.error =
            if (password.isNotEmpty() && !isPasswordValid)
                "Password must contain:\n- 8 characters\n- uppercase\n- lowercase\n- number\n- special char"
            else null

        etPhone.error = if (phone.isNotEmpty() && !isPhoneFormatValid) "Invalid phone number" else null

        val isFormValid =
            isUsernameValid &&
                    isEmailFormatValid &&
                    isPasswordValid &&
                    isPhoneFormatValid

        btnNext.isEnabled = isFormValid
        btnNext.alpha = if (isFormValid) 1f else 0.5f
    }

    private fun isPasswordStrong(password: String): Boolean {
        val uppercase = Regex(".*[A-Z].*")
        val lowercase = Regex(".*[a-z].*")
        val number = Regex(".*[0-9].*")
        val special = Regex(""".*[!@#$%^&*(),.?":{}|<>].*""")

        return password.length >= 8 &&
                password.matches(uppercase) &&
                password.matches(lowercase) &&
                password.matches(number) &&
                password.matches(special)
    }
}
