package sh.calvin.autolinktext

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import androidx.core.net.toUri

fun interface MatchClickHandler {
    fun handleClick(s: String, match: TextMatchResult)
}

object MatchClickHandlerDefaults {
    val NoOp = MatchClickHandler { _, _ -> }
    fun WebUrl(context: Context) =
        MatchClickHandler { s, match ->
            val url = s.slice(match)
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
        }

    fun EmailAddress(context: Context) =
        MatchClickHandler { s, match ->
            val email = s.slice(match)
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            }
            context.startActivity(intent)
        }

    fun PhoneNumber(context: Context) =
        MatchClickHandler { s, match ->
            val phone = s.slice(match).let {
                when (match) {
                    is TextMatchResult.MatcherMatch -> Patterns.digitsAndPlusOnly(match.matcher)
                    is TextMatchResult.TextMatch, is TextMatchResult.RegexMatch -> it
                }
            }

            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            context.startActivity(intent)
        }
}