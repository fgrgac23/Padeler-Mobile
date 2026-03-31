package com.example.rampu2506_padeler.fragments.navbarFragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.rampu2506_padeler.R
import androidx.lifecycle.lifecycleScope
import com.example.rampu2506_padeler.MainActivity
import com.example.rampu2506_padeler.repositories.RepoProvider
import com.example.rampu2506_padeler.fragments.EditProfileFragment
import com.example.rampu2506_padeler.fragments.FilterFragment
import com.example.rampu2506_padeler.fragments.NavigationBar
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch


class MoreFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_more, container, false)
    }
    private lateinit var prefs: android.content.SharedPreferences
    private var loggedUserId: Int = 0
    private lateinit var profileName: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prefs = requireActivity().getSharedPreferences("user_prefs", 0)
        loggedUserId = prefs.getInt("logged_user_id", 0)
        profileName = view.findViewById(R.id.profileName)
        val btnLogout = view.findViewById<MaterialCardView>(R.id.cardLogout)
        val btnEditProfile = view.findViewById<MaterialCardView>(R.id.cardEditProfile)
        val btnFilter = view.findViewById<MaterialCardView>(R.id.cardFilter)
        val btnAbout = view.findViewById<MaterialCardView>(R.id.cardAbout)
        val btnFeedback = view.findViewById<MaterialCardView>(R.id.cardFeedback)
        val btnDelete = view.findViewById<MaterialCardView>(R.id.cardDelete)


        refreshProfileName()

        btnLogout.setOnClickListener {
            logout()
        }

        btnEditProfile.setOnClickListener {
            (activity as NavigationBar).openFragment(EditProfileFragment())
        }

        parentFragmentManager.setFragmentResultListener(
            "profile_updated",
            viewLifecycleOwner
        ) { _, _ ->
            refreshProfileName()
        }

        btnFilter.setOnClickListener {
            (activity as NavigationBar).openFragment(FilterFragment())
        }

        btnAbout.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("About Padeler")
                .setMessage(
                    "Padeler\n\n" +
                            "Application for connecting padel players.\n" +
                            "Version: 1.0\n" +
                            "© 2026 RAMPU"
                )
                .setPositiveButton("OK", null)
                .show()
        }

        btnFeedback.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("padeler212@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Padeler – Feedback")
                putExtra(Intent.EXTRA_TEXT, "Hello,\n\nI want to leave feedback:\n")
            }

            try {
                startActivity(Intent.createChooser(intent, "Send email"))
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "No mail application.", Toast.LENGTH_SHORT).show()
            }
        }

        btnDelete.setOnClickListener {
            val dialogClickListener: DialogInterface.OnClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                RepoProvider.users.deleteUser(loggedUserId)
                                logout()
                            }
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {}
                    }
                }
            val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshProfileName()
    }

    private fun refreshProfileName() {
        viewLifecycleOwner.lifecycleScope.launch {
            val user = RepoProvider.users.getByIdLocal(loggedUserId)
            profileName.text = if (user != null) {
                "${user.name.orEmpty()} ${user.surname.orEmpty()}".trim()
            } else ""
        }
    }

    private fun logout(){
        prefs.edit().remove("logged_user_id").apply()
        val i = Intent(requireContext(), MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }
}