import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    init() {
        MainViewControllerKt.doInitKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
