package sh.calvin.autolinktext

import platform.Foundation.NSURL
import platform.Foundation.NSURLComponents

object IosMatchAnnotationProviderDefaults : MatchAnnotationProviderDefaultsInterface {
    override val WebUrl: MatchAnnotationProvider<*>
        get() = {
            val url = when (val data = it.data) {
                is NSURL -> data
                else -> NSURLComponents(it.matchedText).apply {
                    scheme = scheme ?: "https"
                }.URL
            }
            url?.absoluteString
        }

    override val EmailAddress: MatchAnnotationProvider<*>
        get() = {
            val url = when (val data = it.data) {
                is NSURL -> data
                else -> NSURL(string = "mailto:${it.matchedText}")
            }
            url.absoluteString
        }

    override val PhoneNumber: MatchAnnotationProvider<*>
        get() = {
            val url = when (val data = it.data) {
                is NSURL -> data
                else -> NSURL(string = "tel:${normalizePhoneNumber(it.matchedText)}")
            }
            url.absoluteString
        }
}

internal actual fun getMatchAnnotationProviderDefaults(): MatchAnnotationProviderDefaultsInterface =
    IosMatchAnnotationProviderDefaults
