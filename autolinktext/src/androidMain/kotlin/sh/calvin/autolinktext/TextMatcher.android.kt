package sh.calvin.autolinktext

import android.annotation.SuppressLint
import android.os.Build
import android.telephony.TelephonyManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.util.PatternsCompat
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.Locale
import java.util.regex.Pattern

private fun matchPattern(
    text: String,
    pattern: Pattern,
    matchFilter: MatchFilter<Any?> = MatchFilterDefaults.NoOp
): List<SimpleTextMatchResult<Any?>> {
    val matcher = pattern.matcher(text)
    val matches = mutableListOf<SimpleTextMatchResult<Any?>>()
    while (matcher.find()) {
        val result = SimpleTextMatchResult(
            matcher.start(),
            matcher.end()
        )
        if (matchFilter(text, result)) {
            matches.add(result)
        }
    }
    return matches
}

actual object TextMatcherDefaults {
    @SuppressLint("RestrictedApi")
    actual fun webUrl(contextData: ContextData): TextMatcher<Any?> {
        return TextMatcher.FunctionMatcher { text ->
            matchPattern(text, PatternsCompat.AUTOLINK_WEB_URL, MatchFilterDefaults.WebUrls)
        }
    }

    @SuppressLint("RestrictedApi")
    actual fun emailAddress(contextData: ContextData): TextMatcher<Any?> {
        return TextMatcher.FunctionMatcher { text ->
            matchPattern(text, PatternsCompat.AUTOLINK_EMAIL_ADDRESS)
        }
    }

    actual fun phoneNumber(contextData: ContextData): TextMatcher<Any?> {
        val simCountryIso = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (contextData as AndroidContextData).context.getSystemService(TelephonyManager::class.java).simCountryIso.let {
                if (it.isNotEmpty()) it.uppercase() else null
            }
        } else {
            null
        }

        return TextMatcher.FunctionMatcher { text ->
            val phoneUtil = PhoneNumberUtil.getInstance()
            val regionCode = simCountryIso ?: Locale.getDefault().country
            val matches = phoneUtil.findNumbers(
                text,
                regionCode, PhoneNumberUtil.Leniency.POSSIBLE, Long.MAX_VALUE
            )

            matches.mapNotNull {
                val result = SimpleTextMatchResult(it.start(), it.end())
                if (MatchFilterDefaults.PhoneNumber(text, result)) result else null
            }
        }
    }

    @Composable
    actual fun webUrl() = webUrl(AndroidContextData(LocalContext.current))

    @Composable
    actual fun emailAddress() = emailAddress(AndroidContextData(LocalContext.current))

    @Composable
    actual fun phoneNumber() = phoneNumber(AndroidContextData(LocalContext.current))
}
