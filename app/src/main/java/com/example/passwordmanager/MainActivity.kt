package com.example.passwordmanager

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.passwordmanager.databinding.ActivityMainBinding
import com.example.passwordmanager.ui.LoginActivity
import com.example.passwordmanager.security.SessionManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private val logoutHandler = Handler(Looper.getMainLooper())
    private val logoutRunnable = Runnable {
        sessionManager.clearSession()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize session manager
        sessionManager = SessionManager.getInstance(this)
        
        // Check if user is authenticated (always required for encrypted system)
        if (!sessionManager.isAuthenticated()) {
            // Redirect to login
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            return
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
    }

    override fun onResume() {
        super.onResume()
        sessionManager.onAppForeground()
        resetLogoutTimer()
    }
    
    override fun onPause() {
        super.onPause()
        sessionManager.onAppBackground()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        sessionManager.updateLastActivityTime()
        resetLogoutTimer()
    }

    private fun resetLogoutTimer() {
        logoutHandler.removeCallbacks(logoutRunnable)
        logoutHandler.postDelayed(logoutRunnable, 2 * 60 * 1000) // 2 minutes
    }
}
