package sh.calvin.autolinktext

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.PhoneNumberUtils
import androidx.core.net.toUri

typealias MatchClickHandler = (TextMatchResult) -> Unit

object MatchClickHandlerDefaults {
    val NoOp: MatchClickHandler = { }
    fun webUrl(context: Context): MatchClickHandler =
        {
            val url = it.matchedText
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
        }

    fun emailAddress(context: Context): MatchClickHandler =
        {
            val email = it.matchedText
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            }
            context.startActivity(intent)
        }

    fun phoneNumber(context: Context): MatchClickHandler =
        {
            val phone = PhoneNumberUtils.normalizeNumber(it.matchedText)
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            context.startActivity(intent)
        }
}