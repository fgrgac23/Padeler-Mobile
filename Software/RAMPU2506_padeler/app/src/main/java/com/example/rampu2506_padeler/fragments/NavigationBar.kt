package com.example.rampu2506_padeler.fragments

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.rampu2506_padeler.R
import com.example.rampu2506_padeler.database.AppDatabase
import com.example.rampu2506_padeler.fragments.navbarFragments.HomeFragment
import com.example.rampu2506_padeler.fragments.navbarFragments.MatchFragment
import com.example.rampu2506_padeler.fragments.navbarFragments.MoreFragment
import com.example.rampu2506_padeler.workers.NotificationWorker
import java.util.concurrent.TimeUnit

class NavigationBar : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabMatch: ImageView
    private lateinit var tabSwipe: ImageView
    private lateinit var tabMore: ImageView
    private lateinit var textMatch: TextView
    private lateinit var textSwipe: TextView
    private lateinit var textMore: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navbar)

        AppDatabase.buildInstance(this)

        val periodic = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "padeler_notifications",
            ExistingPeriodicWorkPolicy.UPDATE,
            periodic
        )

        viewPager = findViewById(R.id.viewPager)
        tabMatch = findViewById(R.id.tabMatch)
        tabSwipe = findViewById(R.id.tabSwipe)
        tabMore = findViewById(R.id.tabMore)
        textMatch = findViewById(R.id.textMatch)
        textSwipe = findViewById(R.id.textSwipe)
        textMore = findViewById(R.id.textMore)

        val fragments = listOf(
            MatchFragment(),
            HomeFragment(),
            MoreFragment()
        )

        viewPager.adapter = object : FragmentStateAdapter(this){
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int): Fragment = fragments[position]
        }

        viewPager.setCurrentItem(1, false)
        highlightTab(tabSwipe, textSwipe)
        viewPager.isUserInputEnabled = false

        tabMatch.setOnClickListener {
            viewPager.setCurrentItem(0, true)
            highlightTab(tabMatch, textMatch)
        }

        tabSwipe.setOnClickListener {
            viewPager.setCurrentItem(1, true)
            highlightTab(tabSwipe, textSwipe)
        }

        tabMore.setOnClickListener {
            viewPager.setCurrentItem(2, true)
            highlightTab(tabMore, textMore)
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> highlightTab(tabMatch, textMatch)
                    1 -> highlightTab(tabSwipe, textSwipe)
                    2 -> highlightTab(tabMore, textMore)
                }
                viewPager.isUserInputEnabled = (position != 1)
            }
        })
        supportFragmentManager.addOnBackStackChangedListener {
            val container = findViewById<FrameLayout>(R.id.fragmentContainer)
            if (supportFragmentManager.backStackEntryCount == 0) {
                container.visibility = View.GONE
                viewPager.visibility = View.VISIBLE
            }
        }
    }

    private fun highlightTab(selected: ImageView, selectedText: TextView) {
        val tabs = listOf(tabMatch, tabSwipe, tabMore)
        val texts = listOf(textMatch, textSwipe, textMore)

        for (tab in tabs) {
            tab.animate().scaleX(1f).scaleY(1f).translationY(0f).setDuration(150).start()
        }
        for (text in texts) {
            text.animate().alpha(0f).setDuration(100)
                .withEndAction { text.visibility = View.INVISIBLE }
                .start()
        }

        selected.animate()
            .scaleX(1.4f)
            .scaleY(1.4f)
            .translationY(-20f)
            .setDuration(200)
            .withEndAction {
                selected.animate()
                    .scaleX(1.25f)
                    .scaleY(1.25f)
                    .translationY(-10f)
                    .setDuration(120)
                    .start()
            }.start()

        selectedText.visibility = View.VISIBLE
        selectedText.alpha = 0f
        selectedText.animate().alpha(1f).setDuration(250).start()
    }

    fun openFragment(fragment: Fragment) {
        val container = findViewById<FrameLayout>(R.id.fragmentContainer)

        container.visibility = View.VISIBLE
        viewPager.visibility = View.GONE

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}