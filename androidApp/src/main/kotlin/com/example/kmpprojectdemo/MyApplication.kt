
package com.example.kmpprojectdemo

import android.app.Application
import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.push.firebase.FirebasePush

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KMPNotifier.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                notificationIconResId = R.mipmap.ic_launcher,
                showPushNotification = true
            ),
            FirebasePush
        )
    }
}
