package sh.calvin.autolinktext

internal actual fun getMatchUrlProviderDefaults() =
    object : MatchUrlProviderDefaultsInterface {
        override val WebUrl: MatchUrlProvider<*>
            get() = {
                var url = it.matchedText
                val protocolRegex = Regex("^\\S+://.+$")
                val hasProtocol = protocolRegex.matches(url)
                if (!hasProtocol) {
                    url = "https://$url"
                }
                url
            }

        override val EmailAddress: MatchUrlProvider<*>
            get() = {
                "mailto:${it.matchedText}"
            }

        override val PhoneNumber: MatchUrlProvider<*>
            get() = {
                "tel:${normalizePhoneNumber(it.matchedText)}"
            }
    }
