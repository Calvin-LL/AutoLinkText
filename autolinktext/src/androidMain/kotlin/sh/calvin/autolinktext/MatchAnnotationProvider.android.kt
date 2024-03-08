package sh.calvin.autolinktext

import android.telephony.PhoneNumberUtils
import androidx.core.net.toUri
import java.net.URL

internal actual fun getMatchAnnotationProviderDefaults() =
    object : MatchAnnotationProviderDefaultsInterface {
        override val WebUrl: MatchAnnotationProvider<*>
            get() = {
                val url = it.matchedText.toUri().let {
                    if (it.scheme == null)
                        URL("https://${it}")
                    else
                        it
                }
                url.toString()
            }

        override val EmailAddress: MatchAnnotationProvider<*>
            get() = {
                "mailto:${it.matchedText}"
            }

        override val PhoneNumber: MatchAnnotationProvider<*>
            get() = {
                val phone = PhoneNumberUtils.normalizeNumber(it.matchedText)
                "tel:${phone}"
            }
    }
