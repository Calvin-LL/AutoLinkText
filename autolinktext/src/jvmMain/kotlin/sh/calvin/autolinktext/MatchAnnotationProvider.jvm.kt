package sh.calvin.autolinktext

import java.net.URI

internal actual fun getMatchAnnotationProviderDefaults() =
    object : MatchAnnotationProviderDefaultsInterface {
        override val WebUrl: MatchAnnotationProvider<*>
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

        override val EmailAddress: MatchAnnotationProvider<*>
            get() = {
                "mailto:${it.matchedText}"
            }

        override val PhoneNumber: MatchAnnotationProvider<*>
            get() = {
                "tel:${normalizePhoneNumber(it.matchedText)}"
            }
    }
