package sh.calvin.autolinktext

import android.telephony.PhoneNumberUtils
import androidx.core.net.toUri
import java.net.URL

internal actual fun getMatchUrlProviderDefaults() =
    object : MatchUrlProviderDefaultsInterface {
        override val WebUrl: MatchUrlProvider<*>
            get() = { result ->
                val url = result.matchedText.toUri().let { uri ->
                    if (uri.scheme == null)
                        URL("https://${result.matchedText}").toString().toUri()
                    else
                        uri
                }
                url.toString()
            }

        override val EmailAddress: MatchUrlProvider<*>
            get() = {
                "mailto:${it.matchedText}"
            }

        override val PhoneNumber: MatchUrlProvider<*>
            get() = {
                val phone = PhoneNumberUtils.normalizeNumber(it.matchedText)
                "tel:${phone}"
            }
    }
