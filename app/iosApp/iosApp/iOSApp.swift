import SwiftUI
import ComposeApp
import FirebaseCore

@main
struct iOSApp: App {
    init() {
        InitKoinKt.doInitKoin()
        FirebaseApp.configure()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
