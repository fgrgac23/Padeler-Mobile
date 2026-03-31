package com.example.rampu2506_padeler.fragments.navbarFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.rampu2506_padeler.R
import com.example.rampu2506_padeler.entities.User
import com.example.rampu2506_padeler.fragments.MatchDialogFragment
import com.example.rampu2506_padeler.repositories.RepoProvider
import androidx.lifecycle.lifecycleScope
import com.example.rampu2506_padeler.fragments.ProfileCard
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var loggedUserId: Int = -1
    private var startX = 0f
    private val nearbyUsers = mutableListOf<User>()
    private var currentIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val prefs = requireActivity().getSharedPreferences("user_prefs", 0)
        loggedUserId = prefs.getInt("logged_user_id", -1)

        if(loggedUserId == -1){
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_LONG).show()
            return view
        }

        val card = view.findViewById<View>(R.id.fragmentCardContainer)

        card.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val deltaX = event.x - startX

                    when{
                        deltaX > 150 -> {
                            onSwipe("LIKE")
                        }
                        deltaX < -150 -> {
                            onSwipe("DISLIKE")
                        }
                    }
                    true
                }
                else -> false
                }
            }
        loadNearbyUsers()
        return view
        }

    private fun loadNearbyUsers(){
        lifecycleScope.launch{
            try{
                val users = RepoProvider.users.fetchNearby(
                    currentUserId = loggedUserId,
                    lat = RepoProvider.users.getByIdLocal(loggedUserId)?.latitude ?: return@launch,
                    lng = RepoProvider.users.getByIdLocal(loggedUserId)?.longitude ?: return@launch,
                    radius = 10
                )

                nearbyUsers.clear()
                nearbyUsers.addAll(users.filter { it.userId != loggedUserId })

                if(nearbyUsers.isEmpty()){
                    Toast.makeText(requireContext(), "No nearby users", Toast.LENGTH_LONG).show()
                    return@launch
                }
                showCurrentUser()
            }catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error loading nearby users: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showCurrentUser(){
        if(currentIndex >= nearbyUsers.size){
            Toast.makeText(requireContext(), "No more users", Toast.LENGTH_LONG).show()
            return
        }
        val user = nearbyUsers[currentIndex]

        val fragment = ProfileCard().apply{
            arguments = Bundle().apply{
                putInt("userId", user.userId)
            }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentCardContainer, fragment)
            .commit()
    }
    private fun onSwipe(response: String) {
        if(currentIndex >= nearbyUsers.size) return

        val targetUser = nearbyUsers[currentIndex]

        lifecycleScope.launch {
            val result = RepoProvider.matches.swipe(loggedUserId, targetUser.userId, response)

            if(result?.status == "MATCHED"){
                shownMatchOverlay()
            }
            currentIndex++
            showCurrentUser()
        }
    }
    private fun shownMatchOverlay() {
        val dialog = MatchDialogFragment()
        dialog.isCancelable = false
        dialog.show(parentFragmentManager, "match_dialog")
    }
}



