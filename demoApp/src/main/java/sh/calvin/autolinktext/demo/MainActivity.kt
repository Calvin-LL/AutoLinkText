package sh.calvin.autolinktext.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sh.calvin.autolinktext.AutoLinkText
import sh.calvin.autolinktext.TextMatcher
import sh.calvin.autolinktext.TextRule
import sh.calvin.autolinktext.demo.theme.AutoLinkTextTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AutoLinkTextTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AutoLinkText(
                text = "Visit https://www.google.com\n" +
                        "Visit www.google.com\n" +
                        "Email test@example.com\n" +
                        "Call 6045557890\n" +
                        "Call +1 (604) 555-7890\n" +
                        "Call 604-555-7890\n",
                style = LocalTextStyle.current.copy(
                    color = LocalContentColor.current,
                ),
            )

            AutoLinkText(
                text = "Visit https://www.google.com\n" +
                        "Visit www.google.com\n" +
                        "Email test@example.com\n" +
                        "Call 6045557890\n" +
                        "Call +1 (604) 555-7890\n" +
                        "Call 604-555-7890\n",
                style = LocalTextStyle.current.copy(
                    color = LocalContentColor.current,
                    textAlign = TextAlign.Center,
                )
            )

            val linkColor = MaterialTheme.colorScheme.primary

            AutoLinkText(
                text = "Visit https://www.google.com\n" +
                        "Visit www.google.com\n" +
                        "Email test@example.com\n" +
                        "Call 6045557890\n" +
                        "Call +1 (604) 555-7890\n" +
                        "Call 604-555-7890\n",
                style = LocalTextStyle.current.copy(
                    color = LocalContentColor.current,
                ),
                textRules = TextRule.defaultList(LocalContext.current).map {
                    it.copy(
                        matchStyleProvider = {
                            SpanStyle(
                                color = linkColor,
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    )
                }
            )

            var clickCount by remember { mutableStateOf(0) }

            AutoLinkText(
                text = "Make this clickable, this too but not THIS. Click count: $clickCount.",
                style = LocalTextStyle.current.copy(
                    color = LocalContentColor.current,
                ),
                textRules = listOf(
                    TextRule(
                        textMatcher = TextMatcher.StringMatcher("this"),
                        matchClickHandler = {
                            clickCount++
                        }
                    )
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AutoLinkTextTheme {
        MainScreen()
    }
}