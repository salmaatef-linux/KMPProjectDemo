package com.example.kmpprojectdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.kmpprojectdemo.data.AndroidAppContext
import com.mmk.kmpnotifier.permission.permissionUtil

import androidx.lifecycle.lifecycleScope
import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.push.firebase.firebasePushNotifier
import kotlinx.coroutines.launch
class MainActivity : ComponentActivity() {

    private val permissionUtil by permissionUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val token = KMPNotifier.firebasePushNotifier.getToken()
            android.util.Log.d("FCM_TOKEN", "Token: $token")
        }
        enableEdgeToEdge()
        AndroidAppContext.context = applicationContext

        permissionUtil.askNotificationPermission { granted ->
            println("Notification permission granted: $granted")
        }

        setContent {
            App()
        }
    }
}
