package sh.calvin.autolinktext

import androidx.compose.runtime.Composable

@OptIn(NotForAndroid::class)
@Composable
internal actual fun platformWebUrl(): TextRule = TextRuleDefaults.webUrl(NullContextData)

@OptIn(NotForAndroid::class)
@Composable
internal actual fun platformEmailAddress(): TextRule =
    TextRuleDefaults.emailAddress(NullContextData)

@OptIn(NotForAndroid::class)
@Composable
internal actual fun platformPhoneNumber(): TextRule = TextRuleDefaults.phoneNumber(NullContextData)

@OptIn(NotForAndroid::class)
@Composable
actual fun platformDefaultList(): List<TextRule> = listOf(
    TextRuleDefaults.webUrl(NullContextData),
    TextRuleDefaults.emailAddress(NullContextData),
    TextRuleDefaults.phoneNumber(NullContextData),
)