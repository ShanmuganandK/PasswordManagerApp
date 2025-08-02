package com.example.passwordmanager.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.passwordmanager.data.PasswordRepository
import com.example.passwordmanager.model.PasswordEntry
import java.util.*

class ExpiryNotifier(private val context: Context, private val passwordRepository: PasswordRepository) {

    private val channelId = "password_expiry_notifications"

    init {
        createNotificationChannel()
    }

    fun checkForExpiredPasswords() {
        val expiredPasswords = passwordRepository.getExpiredPasswords()
        for (password in expiredPasswords) {
            sendExpiryNotification(password)
        }
    }

    private fun sendExpiryNotification(password: PasswordEntry) {
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Password Expiry Alert")
            .setContentText("The password for '${password.context}' has expired.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(password.id.hashCode(), notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Password Expiry Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}