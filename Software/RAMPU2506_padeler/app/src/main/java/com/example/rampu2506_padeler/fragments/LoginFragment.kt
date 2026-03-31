package com.example.rampu2506_padeler.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.rampu2506_padeler.R
import com.example.rampu2506_padeler.repositories.RepoProvider
import kotlinx.coroutines.launch
import com.example.rampu2506_padeler.MainActivity
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource


class LoginFragment : Fragment() {

    private var pendingUserIdForLocation: Int? = null

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            val userId = pendingUserIdForLocation
            pendingUserIdForLocation = null

            if (granted && userId != null) {
                updateLocationAndContinue(userId)
            } else {
                goToNavbar()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_login, container, false)

        val etUser = view.findViewById<EditText>(R.id.etAUser)
        val etPassword = view.findViewById<EditText>(R.id.etAPassword)
        val btnLogin = view.findViewById<Button>(R.id.btnALogin)
        val tvRegister = view.findViewById<TextView>(R.id.tvANoAcc)

        btnLogin.setOnClickListener {
            val username = etUser.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val user = RepoProvider.users.login(username, password)
                    if (user == null) {
                        Toast.makeText(requireContext(), "Incorrect username or password", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val prefs = requireActivity().getSharedPreferences("user_prefs", 0)
                    prefs.edit().putInt("logged_user_id", user.userId).apply()

                    //(requireActivity() as MainActivity).onLoginSuccess()
                    val userId = user.userId
                    val hasPerm = ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    if (hasPerm) {
                        updateLocationAndContinue(userId)
                    } else {
                        pendingUserIdForLocation = userId
                        requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }

                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Login failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        tvRegister.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
                .replace(R.id.mainLayout, RegisterFragment1())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
    private fun goToNavbar() {
        val intent = Intent(requireContext(), NavigationBar::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun updateLocationAndContinue(userId: Int) {
        val fused = LocationServices.getFusedLocationProviderClient(requireActivity())

        fun sendToServer(lat: Double, lng: Double) {
            lifecycleScope.launch {
                try {
                    RepoProvider.users.updateLocation(userId, lat, lng)
                    RepoProvider.users.refreshUser(userId)
                } catch (_: Exception) { }
                (requireActivity() as MainActivity).onLoginSuccess()
                goToNavbar()
            }
        }

        try {
            val cts = CancellationTokenSource()
            fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .addOnSuccessListener { cur ->
                    if (cur != null) {
                        sendToServer(cur.latitude, cur.longitude)
                    } else {
                        fused.lastLocation
                            .addOnSuccessListener { last ->
                                if (last != null) sendToServer(last.latitude, last.longitude)
                                else goToNavbar()
                            }
                            .addOnFailureListener { goToNavbar() }
                    }
                }
                .addOnFailureListener { goToNavbar() }
        } catch (_: SecurityException) {
            goToNavbar()
        }
    }
}
