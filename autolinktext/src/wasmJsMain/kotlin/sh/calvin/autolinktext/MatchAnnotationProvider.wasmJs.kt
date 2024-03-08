package sh.calvin.autolinktext

internal actual fun getMatchAnnotationProviderDefaults() =
    object : MatchAnnotationProviderDefaultsInterface {
        override val WebUrl: MatchAnnotationProvider<*>
            get() = {
                var url = it.matchedText
                val protocolRegex = Regex("^\\S+://.+$")
                val hasProtocol = protocolRegex.matches(url)
                if (!hasProtocol) {
                    url = "https://$url"
                }
                url
            }

        override val EmailAddress: MatchAnnotationProvider<*>
            get() = {
                "mailto:${it.matchedText}"
            }

        override val PhoneNumber: MatchAnnotationProvider<*>
            get() = {
                "tel:${normalizePhoneNumber(it.matchedText)}"
            }
    }
