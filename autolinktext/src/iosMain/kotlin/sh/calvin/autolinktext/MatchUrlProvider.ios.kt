package sh.calvin.autolinktext

import platform.Foundation.NSURL
import platform.Foundation.NSURLComponents

object IosMatchUrlProviderDefaults : MatchUrlProviderDefaultsInterface {
    override val WebUrl: MatchUrlProvider<*>
        get() = {
            val url = when (val data = it.data) {
                is NSURL -> data
                else -> NSURLComponents(it.matchedText).apply {
                    scheme = scheme ?: "https"
                }.URL
            }
            url?.absoluteString
        }

    override val EmailAddress: MatchUrlProvider<*>
        get() = {
            val url = when (val data = it.data) {
                is NSURL -> data
                else -> NSURL(string = "mailto:${it.matchedText}")
            }
            url.absoluteString
        }

    override val PhoneNumber: MatchUrlProvider<*>
        get() = {
            val url = when (val data = it.data) {
                is NSURL -> data
                else -> NSURL(string = "tel:${normalizePhoneNumber(it.matchedText)}")
            }
            url.absoluteString
        }
}

internal actual fun getMatchUrlProviderDefaults(): MatchUrlProviderDefaultsInterface =
    IosMatchUrlProviderDefaults
