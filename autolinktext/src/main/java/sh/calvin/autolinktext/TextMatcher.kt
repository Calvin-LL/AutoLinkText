package sh.calvin.autolinktext

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.core.util.PatternsCompat
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.Leniency
import java.util.Locale
import java.util.regex.Pattern


sealed class TextMatcher {
    companion object {
        @SuppressLint("RestrictedApi")
        val WebUrl = PatternMatcher(PatternsCompat.AUTOLINK_WEB_URL)

        @SuppressLint("RestrictedApi")
        val EmailAddress = PatternMatcher(PatternsCompat.AUTOLINK_EMAIL_ADDRESS)

        fun PhoneNumber(context: Context): FunctionMatcher {
            val simCountryIso = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.getSystemService(TelephonyManager::class.java).simCountryIso.let {
                    if (it.isNotEmpty()) it.uppercase() else null
                }
            } else {
                null
            }

            return FunctionMatcher { text ->
                val phoneUtil = PhoneNumberUtil.getInstance()
                val regionCode = simCountryIso ?: Locale.getDefault().country
                val matches = phoneUtil.findNumbers(
                    text,
                    regionCode, Leniency.POSSIBLE, Long.MAX_VALUE
                )

                matches.map { SimpleTextMatchResult.TextMatch(it.start(), it.end()) }
            }
        }
    }

    abstract fun apply(text: String): List<SimpleTextMatchResult>

    /**
     * A [TextMatcher] that matches a [Pattern] in the text.
     */
    class PatternMatcher(val pattern: Pattern) : TextMatcher() {
        override fun apply(text: String): List<SimpleTextMatchResult> {
            val matcher = pattern.matcher(text)
            val matches = mutableListOf<SimpleTextMatchResult>()
            while (matcher.find()) {
                matches.add(
                    SimpleTextMatchResult.MatcherMatch(
                        matcher,
                        matcher.start(),
                        matcher.end()
                    )
                )
            }
            return matches
        }
    }

    /**
     * A [TextMatcher] that matches a [Regex] in the text.
     */
    class RegexMatcher(val regex: Regex) : TextMatcher() {
        override fun apply(text: String): List<SimpleTextMatchResult> {
            val matches = mutableListOf<SimpleTextMatchResult>()
            regex.findAll(text).forEach {
                matches.add(SimpleTextMatchResult.RegexMatch(it))
            }
            return matches
        }
    }

    /**
     * A [TextMatcher] that matches all instances of a string in the text.
     */
    class StringMatcher(val string: String) : TextMatcher() {
        override fun apply(text: String): List<SimpleTextMatchResult> {
            val matches = mutableListOf<SimpleTextMatchResult>()
            var index = text.indexOf(string)
            while (index != -1) {
                matches.add(SimpleTextMatchResult.TextMatch(index, index + string.length))
                index = text.indexOf(string, index + 1)
            }
            return matches
        }
    }

    /**
     * A [TextMatcher] that matches based on a function.
     */
    class FunctionMatcher(val function: (String) -> List<SimpleTextMatchResult>) : TextMatcher() {
        override fun apply(text: String): List<SimpleTextMatchResult> {
            return function(text)
        }
    }
}