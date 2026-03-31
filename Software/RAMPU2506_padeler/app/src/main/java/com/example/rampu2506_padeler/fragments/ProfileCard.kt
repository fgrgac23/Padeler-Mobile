package com.example.rampu2506_padeler.fragments

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.rampu2506_padeler.R
import com.example.rampu2506_padeler.entities.User
import com.example.rampu2506_padeler.repositories.RepoProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.*
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class ProfileCard : Fragment() {

    private var isBackVisible: Boolean = false
    private var users: List<User> = emptyList()
    private var currentUserIndex = 0

    private var myLat: Double? = null
    private var myLng: Double? = null

    private var rootView: View? = null
    private var loggedUserIdGlobal: Int = -1
    private var cardContainerRef: View? = null

    private var skipNextOnResumeReload: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("user_prefs", 0)
        val loggedUserId = prefs.getInt("logged_user_id", -1)

        rootView = view
        loggedUserIdGlobal = loggedUserId
        cardContainerRef = view.findViewById(R.id.cardContainer)

        if (loggedUserId == -1) {
            view.findViewById<View>(R.id.cardContainer)?.visibility = View.GONE
            Toast.makeText(requireContext(), "Not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        parentFragmentManager.setFragmentResultListener(
            "filters_applied",
            viewLifecycleOwner
        ) { _, bundle ->
            val radiusFromApply = bundle.getInt("radius_km", 10)
            skipNextOnResumeReload = true
            updateLocationWithGpsAndReload(radiusFromApply)
        }

        val cardFront = view.findViewById<View>(R.id.cFront)
        val cardBack = view.findViewById<View>(R.id.cBack)
        val cardContainer = view.findViewById<View>(R.id.cardContainer)
        cardContainer.visibility = View.INVISIBLE

        val cLike = view.findViewById<View>(R.id.cLike)
        val cDislike = view.findViewById<View>(R.id.cDislike)
        val flipButton = view.findViewById<View>(R.id.btnFlipCard)

        val scale = resources.displayMetrics.density
        val cameraDist = 8000 * scale
        cardContainer.cameraDistance = cameraDist
        cardFront.cameraDistance = cameraDist
        cardBack.cameraDistance = cameraDist

        flipButton.setOnClickListener {
            val (visibleView, invisibleView) =
                if (isBackVisible) Pair(cardBack, cardFront) else Pair(cardFront, cardBack)

            val flipOutAnimator = AnimatorInflater.loadAnimator(activity, R.animator.flip_out)
            val flipInAnimator = AnimatorInflater.loadAnimator(activity, R.animator.flip_in)

            flipOutAnimator.setTarget(cardContainer)
            flipInAnimator.setTarget(cardContainer)

            flipOutAnimator.start()
            flipOutAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (isBackVisible) {
                        cardBack.visibility = View.GONE
                        cardFront.visibility = View.VISIBLE
                        view.findViewById<View>(R.id.cRatingOverlay).visibility = View.VISIBLE
                    } else {
                        cardFront.visibility = View.GONE
                        cardBack.visibility = View.VISIBLE
                        view.findViewById<View>(R.id.cRatingOverlay).visibility = View.GONE
                    }

                    flipInAnimator.start()
                }
            })

            isBackVisible = !isBackVisible
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val filterPrefs = requireActivity().getSharedPreferences("padeler_prefs", 0)
            val radiusKm = filterPrefs.getInt("radius_km", 10)
            reloadNearby(radiusKmOverride = radiusKm)
        }

        var downX = 0f
        var downY = 0f

        cardContainer.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.rawX
                    downY = event.rawY
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - downX
                    val dy = event.rawY - downY

                    v.translationX = dx
                    v.translationY = dy
                    v.rotation = (dx / v.width) * 18f

                    val threshold = v.width * 0.18f

                    when {
                        dx > threshold -> {
                            cLike.isVisible = true
                            cDislike.isVisible = false
                            cLike.alpha = ((dx - threshold) / (v.width * 0.35f)).coerceIn(0f, 1f)
                        }
                        dx < -threshold -> {
                            cDislike.isVisible = true
                            cLike.isVisible = false
                            cDislike.alpha = ((-dx - threshold) / (v.width * 0.35f)).coerceIn(0f, 1f)
                        }
                        else -> {
                            cLike.isVisible = false
                            cDislike.isVisible = false
                            cLike.alpha = 0f
                            cDislike.alpha = 0f
                        }
                    }
                    true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (users.isEmpty()) {
                        v.animate().translationX(0f).translationY(0f).rotation(0f).setDuration(200).start()
                        cLike.isVisible = false
                        cDislike.isVisible = false
                        return@setOnTouchListener true
                    }

                    val dx = v.translationX
                    val swipeThreshold = v.width * 0.25f
                    val targetUser = users[currentUserIndex]

                    when {
                        dx > swipeThreshold -> {
                            v.animate()
                                .translationX(v.width.toFloat() * 1.6f)
                                .rotation(20f)
                                .alpha(0f)
                                .setDuration(280)
                                .setInterpolator(AccelerateInterpolator())
                                .withEndAction {
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        try {
                                            val result = RepoProvider.matches.swipe(
                                                loggedUserId,
                                                targetUser.userId,
                                                "LIKE"
                                            )

                                            if (result?.matched == true || result?.status == "MATCHED") {
                                                MatchDialogFragment().apply { isCancelable = false }
                                                    .show(parentFragmentManager, "MATCH_DIALOG")
                                            }
                                        } catch (_: Exception) {
                                        }
                                        resetCard(v, cLike, cDislike)
                                        moveToNext(view)
                                    }
                                }
                                .start()
                        }

                        dx < -swipeThreshold -> {
                            viewLifecycleOwner.lifecycleScope.launch {
                                try {
                                    RepoProvider.matches.swipe(loggedUserId, targetUser.userId, "DISLIKE")
                                } catch (_: Exception) {
                                }
                            }

                            v.animate()
                                .translationX(-v.width.toFloat() * 1.6f)
                                .rotation(-20f)
                                .alpha(0f)
                                .setDuration(280)
                                .setInterpolator(AccelerateInterpolator())
                                .withEndAction {
                                    resetCard(v, cLike, cDislike)
                                    moveToNext(view)
                                }
                                .start()
                        }

                        else -> {
                            v.animate().translationX(0f).translationY(0f).rotation(0f).setDuration(200).start()
                            cLike.isVisible = false
                            cDislike.isVisible = false
                        }
                    }
                    true
                }

                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (skipNextOnResumeReload) {
            skipNextOnResumeReload = false
            return
        }

        val filterPrefs = requireActivity().getSharedPreferences("padeler_prefs", 0)
        val radiusKm = filterPrefs.getInt("radius_km", 10)
        reloadNearby(radiusKmOverride = radiusKm)
    }

    private fun updateLocationWithGpsAndReload(radiusKm: Int) {
        val loggedUserId = loggedUserIdGlobal
        if (loggedUserId == -1) return

        val fineGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted) {
            reloadNearby(radiusKmOverride = radiusKm)
            return
        }

        val fused = LocationServices.getFusedLocationProviderClient(requireActivity())
        val token = CancellationTokenSource().token

        fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, token)
            .addOnSuccessListener { loc ->
                if (loc == null) {
                    reloadNearby(radiusKmOverride = radiusKm)
                    return@addOnSuccessListener
                }

                val lat = loc.latitude
                val lng = loc.longitude

                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        RepoProvider.users.updateLocation(loggedUserId, lat, lng)
                        RepoProvider.users.refreshUser(loggedUserId)
                        reloadNearby(radiusKmOverride = radiusKm)
                    } catch (_: Exception) {
                        reloadNearby(radiusKmOverride = radiusKm)
                    }
                }
            }
            .addOnFailureListener {
                reloadNearby(radiusKmOverride = radiusKm)
            }
    }

    private fun reloadNearby(radiusKmOverride: Int? = null) {
        if (!isAdded) return
        val view = rootView ?: return
        val loggedUserId = loggedUserIdGlobal
        if (loggedUserId == -1) return

        val cardContainer = cardContainerRef ?: view.findViewById(R.id.cardContainer)
        val cLike = view.findViewById<View>(R.id.cLike)
        val cDislike = view.findViewById<View>(R.id.cDislike)

        val filterPrefs = requireActivity().getSharedPreferences("padeler_prefs", 0)
        val radiusKm = (radiusKmOverride ?: filterPrefs.getInt("radius_km", 10)).coerceIn(1, 50)

        val gender = filterPrefs.getString("gender", "") ?: ""
        val level = filterPrefs.getString("level", "") ?: ""
        val position = filterPrefs.getString("position", "") ?: ""
        val frequency = filterPrefs.getString("frequency", "") ?: ""

        viewLifecycleOwner.lifecycleScope.launch {
            if (!isAdded) return@launch
            try {
                RepoProvider.users.refreshUser(loggedUserId)

                val me = RepoProvider.users.getByIdLocal(loggedUserId)
                myLat = me?.latitude
                myLng = me?.longitude

                users = if (myLat != null && myLng != null) {
                    RepoProvider.users.fetchNearby(
                        loggedUserId,
                        myLat!!,
                        myLng!!,
                        radius = radiusKm,
                        gender = gender,
                        level = level,
                        position = position,
                        frequency = frequency
                    ).filter { it.userId != loggedUserId }
                } else {
                    emptyList()
                }

                if (!isAdded) return@launch

                if (users.isEmpty()) {
                    cardContainer.visibility = View.GONE
                    Toast.makeText(requireContext(), "There are no users nearby.", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                cardContainer.visibility = View.VISIBLE
                currentUserIndex = 0
                resetCard(cardContainer, cLike, cDislike)
                updateCardData(view, users[currentUserIndex])
            } catch (e: Exception) {
                e.printStackTrace()
                if (!isAdded) return@launch
                cardContainer.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Error retrieving user: ${e.message ?: "unknown error"}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun resetCard(v: View, cLike: View, cDislike: View) {
        v.translationX = 0f
        v.translationY = 0f
        v.rotation = 0f
        v.alpha = 1f
        cLike.isVisible = false
        cDislike.isVisible = false
        cLike.alpha = 0f
        cDislike.alpha = 0f
    }

    private fun moveToNext(view: View) {
        if (users.isEmpty()) return
        currentUserIndex++
        if (currentUserIndex >= users.size) {
            view.findViewById<View>(R.id.cardContainer).visibility = View.GONE
            Toast.makeText(requireContext(), "There are no more users.", Toast.LENGTH_SHORT).show()
            return
        }
        updateCardData(view, users[currentUserIndex])
    }

    private fun updateCardData(view: View, user: User) {
        val ratingValueText = view.findViewById<TextView>(R.id.cRatingValue)
        val ratingStarText = view.findViewById<TextView>(R.id.cRatingStar)
        val nameText = view.findViewById<TextView>(R.id.cName)
        val ageText = view.findViewById<TextView>(R.id.cAge)
        val distanceText = view.findViewById<TextView>(R.id.cDistance)
        val levelText = view.findViewById<TextView>(R.id.cLevel)
        val frequencyText = view.findViewById<TextView>(R.id.cFrequency)
        val positionText = view.findViewById<TextView>(R.id.cPosition)

        val fullName = "${user.name.orEmpty()} ${user.surname.orEmpty()}".trim()
        nameText.text = fullName

        val dob = user.dateOfBirth
        ageText.text = if (dob != null) getString(R.string.format_godine, calculateAge(dob)) else ""

        val lat = user.latitude
        val lng = user.longitude
        distanceText.text = if (myLat != null && myLng != null && lat != null && lng != null) {
            val km = haversineKm(myLat!!, myLng!!, lat, lng)
            getString(R.string.format_km, km)
        } else {
            ""
        }

        val image = view.findViewById<ImageView>(R.id.cProfileImage)

        image.setImageResource(R.drawable.person)

        val rating = user.rating

        if (rating != null && rating > 0) {
            ratingValueText.text = String.format("%.1f", rating)
            ratingValueText.visibility = View.VISIBLE
            ratingStarText.visibility = View.VISIBLE
        } else {
            ratingValueText.visibility = View.GONE
            ratingStarText.visibility = View.GONE
        }


        viewLifecycleOwner.lifecycleScope.launch {
            val (bytes, _) = RepoProvider.users.fetchUserImage(user.userId)
            if (bytes != null){
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                image.setImageBitmap(bmp)
            }
        }
        levelText.text = user.levelOfPlay.orEmpty()
        frequencyText.text = user.frequencyOfPlay.orEmpty()
        positionText.text = user.position.orEmpty()
    }

    private fun calculateAge(birthDate: Date): Int {
        val today = Calendar.getInstance()
        val birth = Calendar.getInstance()
        birth.time = birthDate

        var age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
        val todayDay = today.get(Calendar.DAY_OF_YEAR)
        val birthDay = birth.get(Calendar.DAY_OF_YEAR)
        if (todayDay < birthDay) age--
        return age.coerceAtLeast(0)
    }

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
}
