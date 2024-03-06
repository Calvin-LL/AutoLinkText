package sh.calvin.autolinktext

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

class AndroidContextData(val context: Context) : ContextData

internal actual fun getTextRuleDefaults() = object : TextRuleDefaultsInterface {
    @NotForAndroid
    override fun webUrl(contextData: ContextData): TextRule<Any?> {
        assert(contextData is AndroidContextData) { "ContextData must be AndroidContextData" }

        return super.webUrl(contextData)
    }

    @NotForAndroid
    override fun emailAddress(contextData: ContextData): TextRule<Any?> {
        assert(contextData is AndroidContextData) { "ContextData must be AndroidContextData" }

        return super.emailAddress(contextData)
    }

    @NotForAndroid
    override fun phoneNumber(contextData: ContextData): TextRule<Any?> {
        assert(contextData is AndroidContextData) { "ContextData must be AndroidContextData" }

        return super.phoneNumber(contextData)
    }

    @NotForAndroid
    override fun defaultList(contextData: ContextData): List<TextRule<Any?>> {
        assert(contextData is AndroidContextData) { "ContextData must be AndroidContextData" }

        return super.defaultList(contextData)
    }

    @OptIn(NotForAndroid::class)
    @Composable
    override fun webUrl(): TextRule<Any?> {
        val context = AndroidContextData(context = LocalContext.current)

        return webUrl(context)
    }

    @OptIn(NotForAndroid::class)
    @Composable
    override fun emailAddress(): TextRule<Any?> {
        val context = AndroidContextData(context = LocalContext.current)

        return emailAddress(context)
    }

    @OptIn(NotForAndroid::class)
    @Composable
    override fun phoneNumber(): TextRule<Any?> {
        val context = AndroidContextData(context = LocalContext.current)

        return phoneNumber(context)
    }

    @OptIn(NotForAndroid::class)
    @Composable
    override fun defaultList(): List<TextRule<Any?>> {
        val context = AndroidContextData(context = LocalContext.current)

        return defaultList(context)
    }
}