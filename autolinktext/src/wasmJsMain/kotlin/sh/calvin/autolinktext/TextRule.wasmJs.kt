package sh.calvin.autolinktext

import androidx.compose.runtime.Composable

@OptIn(NotForAndroid::class)
@Composable
internal actual fun platformWebUrl(): TextRule<Any?> = TextRuleDefaults.webUrl(NullContextData)

@OptIn(NotForAndroid::class)
@Composable
internal actual fun platformEmailAddress(): TextRule<Any?> =
    TextRuleDefaults.emailAddress(NullContextData)

@OptIn(NotForAndroid::class)
@Composable
internal actual fun platformPhoneNumber(): TextRule<Any?> = TextRuleDefaults.phoneNumber(NullContextData)

@OptIn(NotForAndroid::class)
@Composable
actual fun platformDefaultList(): List<TextRule<Any?>> = listOf(
    TextRuleDefaults.webUrl(NullContextData),
    TextRuleDefaults.emailAddress(NullContextData),
    TextRuleDefaults.phoneNumber(NullContextData),
)