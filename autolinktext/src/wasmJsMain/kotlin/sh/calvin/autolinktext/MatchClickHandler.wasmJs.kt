package sh.calvin.autolinktext

import kotlinx.browser.window

internal actual fun getMatchClickHandlerDefaults() =
    object : MatchClickHandlerDefaultsInterface {
        @NotForAndroid
        override fun webUrl(contextData: ContextData): MatchClickHandler<Any?> {
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

        @NotForAndroid
        override fun emailAddress(contextData: ContextData): MatchClickHandler<Any?> {
            return {
                window.open("mailto:${it.matchedText}", "_blank")
            }
        }

        @NotForAndroid
        override fun phoneNumber(contextData: ContextData): MatchClickHandler<Any?> {
            return {
                window.open("tel:${normalizePhoneNumber(it.matchedText)}", "_blank")
            }
        }
    }
