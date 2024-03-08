package sh.calvin.autolinktext

import co.touchlab.kermit.Logger
import platform.Foundation.NSURL
import platform.Foundation.NSURLComponents
import platform.UIKit.UIApplication

object IosMatchClickHandlerDefaults : MatchClickHandlerDefaultsInterface {
    @NotForAndroid
    override fun webUrl(contextData: ContextData): MatchClickHandler<Any?> {
        return {
            try {
                val url = when (val data = it.data) {
                    is NSURL -> data
                    else -> NSURLComponents(it.matchedText).apply {
                        scheme = scheme ?: "https"
                    }.URL
                }
                if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
                    UIApplication.sharedApplication.openURL(url)
                } else {
                    Logger.w("MatchClickHandlerDefaults") { "Failed to open URL" }
                }
            } catch (t: Throwable) {
                Logger.e(t, "MatchClickHandlerDefaults") { "Failed to open URL" }
            }
        }
    }

    @NotForAndroid
    override fun emailAddress(contextData: ContextData): MatchClickHandler<Any?> {
        return {
            try {
                val url = when (val data = it.data) {
                    is NSURL -> data
                    else -> NSURL(string = "mailto:${it.matchedText}")
                }
                if (UIApplication.sharedApplication.canOpenURL(url)) {
                    UIApplication.sharedApplication.openURL(url)
                } else {
                    Logger.w("MatchClickHandlerDefaults") { "Failed to open email" }
                }
            } catch (t: Throwable) {
                Logger.e(t, "MatchClickHandlerDefaults") { "Failed to open email" }
            }
        }
    }

    @NotForAndroid
    override fun phoneNumber(contextData: ContextData): MatchClickHandler<Any?> {
        return {
            try {
                val url = when (val data = it.data) {
                    is NSURL -> data
                    else -> NSURL(string = "tel:${normalizePhoneNumber(it.matchedText)}")
                }
                if (UIApplication.sharedApplication.canOpenURL(url)) {
                    UIApplication.sharedApplication.openURL(url)
                } else {
                    Logger.w("MatchClickHandlerDefaults") { "Failed to open phone number" }
                }
            } catch (t: Throwable) {
                Logger.e(t, "MatchClickHandlerDefaults") { "Failed to open phone number" }
            }
        }
    }
}

internal actual fun getMatchClickHandlerDefaults(): MatchClickHandlerDefaultsInterface =
    IosMatchClickHandlerDefaults
