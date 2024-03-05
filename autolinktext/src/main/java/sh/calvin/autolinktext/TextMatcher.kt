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
        val WebUrl = PatternMatcher(PatternsCompat.AUTOLINK_WEB_URL, MatchFilterDefaults.WebUrls)

        @SuppressLint("RestrictedApi")
        val EmailAddress = PatternMatcher(PatternsCompat.AUTOLINK_EMAIL_ADDRESS)

        // from https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/text/util/Linkify.java;l=140;drc=4f9480b13d3cab52255608ac5913922ca4269ac5
        /**
         * Don't treat anything with fewer than this many digits as a
         * phone number.
         */
        private const val PHONE_NUMBER_MINIMUM_DIGITS = 5

        // from https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/text/util/Linkify.java;l=169;drc=4f9480b13d3cab52255608ac5913922ca4269ac5
        /**
         *  Filters out URL matches that don't have enough digits to be a
         *  phone number.
         */
        fun isPhoneNumberLongEnough(text: String, match: SimpleTextMatchResult): Boolean {
            var digitCount = 0

            for (i in match.start until match.end) {
                if (Character.isDigit(text[i])) {
                    digitCount++
                    if (digitCount >= PHONE_NUMBER_MINIMUM_DIGITS) {
                        return true
                    }
                }
            }
            return false
        }

        fun phoneNumber(context: Context): FunctionMatcher {
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

                matches.mapNotNull {
                    val result = SimpleTextMatchResult.TextMatch(it.start(), it.end())
                    if (isPhoneNumberLongEnough(text, result)) result else null
                }
            }
        }
    }

    abstract fun apply(text: String): List<SimpleTextMatchResult>

    /**
     * A [TextMatcher] that matches a [Pattern] in the text.
     */
    class PatternMatcher(
        val pattern: Pattern,
        val matchFilter: MatchFilter = MatchFilterDefaults.NoOp,
    ) : TextMatcher() {
        override fun apply(text: String): List<SimpleTextMatchResult> {
            val matcher = pattern.matcher(text)
            val matches = mutableListOf<SimpleTextMatchResult>()
            while (matcher.find()) {
                val result = SimpleTextMatchResult.TextMatch(
                    matcher.start(),
                    matcher.end()
                )
                if (matchFilter(text, result)) {
                    matches.add(result)
                }
            }
            return matches
        }
    }

    /**
     * A [TextMatcher] that matches a [Regex] in the text.
     */
    class RegexMatcher(
        val regex: Regex,
        val matchFilter: MatchFilter = MatchFilterDefaults.NoOp,
    ) : TextMatcher() {
        override fun apply(text: String): List<SimpleTextMatchResult> {
            val matches = mutableListOf<SimpleTextMatchResult>()
            regex.findAll(text).forEach {
                val result = SimpleTextMatchResult.RegexMatch(it)
                if (matchFilter(text, result)) {
                    matches.add(result)
                }
            }
            return matches
        }
    }

    /**
     * A [TextMatcher] that matches all instances of a string in the text.
     */
    class StringMatcher(
        val string: String,
        val matchFilter: MatchFilter = MatchFilterDefaults.NoOp,
    ) : TextMatcher() {
        override fun apply(text: String): List<SimpleTextMatchResult> {
            val matches = mutableListOf<SimpleTextMatchResult>()
            var index = text.indexOf(string)
            while (index != -1) {
                val result = SimpleTextMatchResult.TextMatch(index, index + string.length)
                if (matchFilter(text, result)) {
                    matches.add(result)
                }
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