package sh.calvin.autolinktext

import android.content.Intent
import android.net.Uri
import android.telephony.PhoneNumberUtils
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

actual object MatchClickHandlerDefaults {
    actual val NoOp: MatchClickHandler = { }
    actual fun webUrl(contextData: ContextData): MatchClickHandler =
        {
            val url = it.matchedText
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            (contextData as AndroidContextData).context.startActivity(intent)
        }

    actual fun emailAddress(contextData: ContextData): MatchClickHandler =
        {
            val email = it.matchedText
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            }
            (contextData as AndroidContextData).context.startActivity(intent)
        }

    actual fun phoneNumber(contextData: ContextData): MatchClickHandler =
        {
            val phone = PhoneNumberUtils.normalizeNumber(it.matchedText)
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            (contextData as AndroidContextData).context.startActivity(intent)
        }



    @Composable
    actual fun webUrl() = webUrl(AndroidContextData(LocalContext.current))

    @Composable
    actual fun emailAddress() = emailAddress(AndroidContextData(LocalContext.current))

    @Composable
    actual fun phoneNumber() = phoneNumber(AndroidContextData(LocalContext.current))
}