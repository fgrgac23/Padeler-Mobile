package com.example.rampu2506_padeler

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rampu2506_padeler.fragments.NavigationBar
import com.example.rampu2506_padeler.database.AppDatabase
import com.example.rampu2506_padeler.fragments.LoginFragment
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.rampu2506_padeler.workers.NotificationWorker

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        requestNotificationPermission()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        AppDatabase.buildInstance(applicationContext)

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val loggedUserId = prefs.getInt("logged_user_id", -1)

        if (loggedUserId != -1) {
            val intent = Intent(this, NavigationBar::class.java)
            startActivity(intent)
            finish()
            return
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainLayout, LoginFragment())
                .commit()
        }
    }

    private fun requestNotificationPermission() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
    }

    fun onLoginSuccess() {
        val oneTime = OneTimeWorkRequestBuilder<NotificationWorker>().setInitialDelay(2, java.util.concurrent.TimeUnit.SECONDS).build()
        WorkManager.getInstance(applicationContext).enqueue(oneTime)

        val intent = Intent(this, NavigationBar::class.java)
        startActivity(intent)
        finish()
    }

}
