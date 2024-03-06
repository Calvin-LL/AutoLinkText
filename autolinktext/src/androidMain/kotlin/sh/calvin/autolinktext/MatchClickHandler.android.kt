package sh.calvin.autolinktext

import android.content.Intent
import android.net.Uri
import android.telephony.PhoneNumberUtils
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

internal actual fun getMatchClickHandlerDefaults() = object : MatchClickHandlerDefaultsInterface {
    @NotForAndroid
    override fun webUrl(contextData: ContextData): MatchClickHandler<Any?> =
        {
            val url = it.matchedText.toUri().let {
                if (it.scheme == null) it.buildUpon().scheme("https").build() else it
            }
            val intent = Intent(Intent.ACTION_VIEW, url)
            (contextData as AndroidContextData).context.startActivity(intent)
        }

    @NotForAndroid
    override fun emailAddress(contextData: ContextData): MatchClickHandler<Any?> =
        {
            val email = it.matchedText
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            }
            (contextData as AndroidContextData).context.startActivity(intent)
        }

    @NotForAndroid
    override fun phoneNumber(contextData: ContextData): MatchClickHandler<Any?> =
        {
            val phone = PhoneNumberUtils.normalizeNumber(it.matchedText)
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            (contextData as AndroidContextData).context.startActivity(intent)
        }

    @NotForAndroid
    @Composable
    override fun webUrl() = webUrl(AndroidContextData(LocalContext.current))

    @NotForAndroid
    @Composable
    override fun emailAddress() = emailAddress(AndroidContextData(LocalContext.current))

    @NotForAndroid
    @Composable
    override fun phoneNumber() = phoneNumber(AndroidContextData(LocalContext.current))
}
