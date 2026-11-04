
package com.example.kmpprojectdemo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.kmpprojectdemo.data.AndroidAppContext
import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.extensions.onCreateOrOnNewIntent
import com.mmk.kmpnotifier.permission.permissionUtil
import com.mmk.kmpnotifier.push.firebase.firebasePushNotifier
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val permissionUtil by permissionUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KMPNotifier.onCreateOrOnNewIntent(intent)

        enableEdgeToEdge()
        AndroidAppContext.context = applicationContext

        permissionUtil.askNotificationPermission { granted ->

            Log.d("PUSH_NOTIFICATION", "Permission granted: $granted")

            if (granted) {
                lifecycleScope.launch {
                    try {
                        // Subscribe this device to the "user" topic
                        KMPNotifier.firebasePushNotifier
                            .subscribeToTopic("user")

                        Log.d(
                            "PUSH_NOTIFICATION",
                            "Successfully subscribed to user topic"
                        )

                        // Get the current FCM token for testing
                        val token = KMPNotifier.firebasePushNotifier.getToken()

                        Log.d("FCM_TOKEN", "Token: $token")

                    } catch (e: Exception) {
                        Log.e(
                            "PUSH_NOTIFICATION",
                            "Registration failed",
                            e
                        )
                    }
                }
            }
        }

        setContent {
            App()
        }
    }
}
