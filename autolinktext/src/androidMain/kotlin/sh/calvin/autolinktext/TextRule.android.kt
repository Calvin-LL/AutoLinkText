package sh.calvin.autolinktext

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

class AndroidContextData(val context: Context) : ContextData

@Composable
internal actual fun platformWebUrl(): TextRule {
    val context = AndroidContextData(context = LocalContext.current)

    return TextRule(
        textMatcher = TextMatcherDefaults.webUrl(context),
        matchClickHandler = MatchClickHandlerDefaults.webUrl(context),
    )
}

@Composable
internal actual fun platformEmailAddress(): TextRule {
    val context = AndroidContextData(context = LocalContext.current)

    return TextRule(
        textMatcher = TextMatcherDefaults.emailAddress(context),
        matchClickHandler = MatchClickHandlerDefaults.emailAddress(context),
    )
}

@Composable
internal actual fun platformPhoneNumber(): TextRule {
    val context = AndroidContextData(context = LocalContext.current)

    return TextRule(
        textMatcher = TextMatcherDefaults.phoneNumber(context),
        matchClickHandler = MatchClickHandlerDefaults.phoneNumber(context),
    )
}

/**
 * This can be safely called on all platforms
 */
@OptIn(NotForAndroid::class)
@Composable
actual fun platformDefaultList(): List<TextRule> {
    val context = AndroidContextData(context = LocalContext.current)

    return listOf(
        TextRuleDefaults.webUrl(context),
        TextRuleDefaults.emailAddress(context),
        TextRuleDefaults.phoneNumber(context),
    )
}