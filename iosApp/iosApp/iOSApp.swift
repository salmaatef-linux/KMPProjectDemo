
import SwiftUI
import UIKit
import Shared
import FirebaseCore
import FirebaseMessaging

class AppDelegate: NSObject, UIApplicationDelegate, MessagingDelegate {

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions:
            [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {

        FirebaseApp.configure()

        Messaging.messaging().delegate = self

        KMPNotifier.shared.initialize(
            configuration: NotificationPlatformConfigurationIos(
                showPushNotification: true,
                askNotificationPermissionOnStart: true,
                notificationSoundName: nil
            ),
            extensions: [FirebasePush.shared]
        )

        return true
    }

    // APNs token registration
    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        Messaging.messaging().apnsToken = deviceToken
        print("PUSH_NOTIFICATION: APNs token registered")
    }

    // FCM token received or refreshed
    func messaging(
        _ messaging: Messaging,
        didReceiveRegistrationToken fcmToken: String?
    ) {
        guard let fcmToken = fcmToken else {
            print("PUSH_NOTIFICATION: FCM token unavailable")
            return
        }

        print("PUSH_NOTIFICATION: FCM token received")

        Messaging.messaging().subscribe(toTopic: "user") { error in
            if let error = error {
                print("PUSH_NOTIFICATION: Topic subscription failed: \(error)")
            } else {
                print("PUSH_NOTIFICATION: Successfully subscribed to user topic")
            }
        }
    }

    // Handle remote notification
    func application(
        _ application: UIApplication,
        didReceiveRemoteNotification userInfo: [AnyHashable: Any]
    ) async -> UIBackgroundFetchResult {

        KMPNotifier.shared.onApplicationDidReceiveRemoteNotification(
            userInfo: userInfo
        )

        return .newData
    }
}

@main
struct iOSApp: App {

    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var delegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
