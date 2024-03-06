package sh.calvin.autolinktext

import co.touchlab.kermit.Logger
import java.awt.Desktop
import java.net.URI

internal actual fun getMatchClickHandlerDefaults() = object : MatchClickHandlerDefaultsInterface {
    @NotForAndroid
    override fun webUrl(contextData: ContextData): MatchClickHandler<Any?> {
        return { result ->
            if (Desktop.isDesktopSupported() && Desktop.getDesktop()
                    .isSupported(Desktop.Action.BROWSE)
            ) {
                val url = URI(result.matchedText).let {
                    if (it.scheme == null || it.scheme.isEmpty()) {
                        URI("https://${result.matchedText}")
                    } else {
                        it
                    }
                }
                Desktop.getDesktop().browse(url)
            } else {
                Logger.w("MatchClickHandlerDefaults") { "Desktop is not supported" }
            }
        }
    }

    @NotForAndroid
    override fun emailAddress(contextData: ContextData): MatchClickHandler<Any?> {
        return {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop()
                    .isSupported(Desktop.Action.MAIL)
            ) {
                Desktop.getDesktop().mail(URI("mailto:${it.matchedText}"))
            } else {
                Logger.w("MatchClickHandlerDefaults") { "Desktop is not supported" }
            }
        }
    }

    @NotForAndroid
    override fun phoneNumber(contextData: ContextData): MatchClickHandler<Any?> {
        return {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop()
                    .isSupported(Desktop.Action.BROWSE)
            ) {
                Desktop.getDesktop().browse(URI("tel:${normalizePhoneNumber(it.matchedText)}"))
            } else {
                Logger.w("MatchClickHandlerDefaults") { "Desktop is not supported" }
            }
        }
    }
}