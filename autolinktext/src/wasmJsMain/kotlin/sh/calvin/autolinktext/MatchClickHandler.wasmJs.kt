package sh.calvin.autolinktext

import androidx.compose.runtime.Composable
import kotlinx.browser.window

actual object MatchClickHandlerDefaults {
    actual fun webUrl(contextData: ContextData): MatchClickHandler<Any?> {
        return {
            var url = it.matchedText
            val protocolRegex = Regex("^\\S+://.+$")
            val hasProtocol = protocolRegex.matches(url)
            if (!hasProtocol) {
                url = "https://$url"
            }
            window.open(url, "_blank")
        }
    }

    actual fun emailAddress(contextData: ContextData): MatchClickHandler<Any?> {
        return {
            window.open("mailto:${it.matchedText}", "_blank")
        }
    }

    actual fun phoneNumber(contextData: ContextData): MatchClickHandler<Any?> {
        return {
            window.open("tel:${normalizePhoneNumber(it.matchedText)}", "_blank")
        }
    }

    @Composable
    actual fun webUrl() = webUrl(NullContextData)

    @Composable
    actual fun emailAddress() = emailAddress(NullContextData)

    @Composable
    actual fun phoneNumber() = phoneNumber(NullContextData)
}