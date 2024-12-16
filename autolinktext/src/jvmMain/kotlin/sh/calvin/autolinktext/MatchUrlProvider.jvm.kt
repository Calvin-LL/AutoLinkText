package sh.calvin.autolinktext

import java.net.URI

internal actual fun getMatchUrlProviderDefaults() =
    object : MatchUrlProviderDefaultsInterface {
        override val WebUrl: MatchUrlProvider<*>
            get() = { result ->
                val url = URI(result.matchedText).let {
                    if (it.scheme == null || it.scheme.isEmpty()) {
                        URI("https://${result.matchedText}")
                    } else {
                        it
                    }
                }
                url.toString()
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
