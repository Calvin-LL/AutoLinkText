package sh.calvin.autolinktext.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import sh.calvin.autolinktext.AutoLinkText
import sh.calvin.autolinktext.SimpleTextMatchResult
import sh.calvin.autolinktext.TextMatcher
import sh.calvin.autolinktext.TextRule
import sh.calvin.autolinktext.TextRuleDefaults
import sh.calvin.autolinktext.demo.ui.theme.AutoLinkTextTheme
import sh.calvin.autolinktext.slice

@Composable
internal fun App() {
    AutoLinkTextTheme {
        MainScreen()
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
                .safeContentPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AutoLinkText(
                text = """
                    |Visit https://www.google.com
                    |Visit www.google.com
                    |Email test@example.com
                    |Call 6045557890
                    |Call +1 (604) 555-7890
                    |Call 604-555-7890
                """.trimMargin(),
                style = LocalTextStyle.current.copy(
                    color = LocalContentColor.current,
                ),
            )

            AutoLinkText(
                text = """
                    |Visit https://www.google.com
                    |Visit www.google.com
                    |Email test@example.com
                    |Call 6045557890
                    |Call +1 (604) 555-7890
                    |Call 604-555-7890
                """.trimMargin(),
                style = LocalTextStyle.current.copy(
                    color = LocalContentColor.current,
                    textAlign = TextAlign.Center,
                )
            )

            AutoLinkText(
                text = """
                    |Visit https://www.google.com
                    |Visit www.google.com
                    |Email test@example.com
                    |Call 6045557890
                    |Call +1 (604) 555-7890
                    |Call 604-555-7890
                """.trimMargin(),
                style = LocalTextStyle.current.copy(
                    color = LocalContentColor.current,
                ),
                textRules = TextRuleDefaults.defaultList().map {
                    it.copy(
                        matchStyle = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
            )

            AutoLinkText(
                text = "Make your own rules like #hashtag and @mention",
                style = LocalTextStyle.current.copy(
                    color = LocalContentColor.current,
                ),
                textRules = listOf(
                    TextRule(
                        textMatcher = TextMatcher.RegexMatcher(Regex("#\\w+")),
                        matchStyle = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        ),
                        onClick = {
                            println("Hashtag ${it.matchedText} clicked")
                        }
                    ),
                    TextRule(
                        textMatcher = TextMatcher.RegexMatcher(Regex("@\\w+")),
                        matchStyle = SpanStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            textDecoration = TextDecoration.Underline
                        ),
                        onClick = {
                            println("Mention ${it.matchedText} clicked")
                        }
                    )
                )
            )

            AutoLinkText(
                text = "Style the same rule differently like #hashtag1 and #hashtag2",
                style = LocalTextStyle.current.copy(
                    color = LocalContentColor.current,
                ),
                textRules = listOf(
                    TextRule(
                        textMatcher = TextMatcher.RegexMatcher(Regex("#\\w+")),
                        matchStyleProvider = {
                            val hashtag = it.matchedText
                            if (hashtag == "#hashtag1") {
                                SpanStyle(
                                    color = Color.Red,
                                    textDecoration = TextDecoration.Underline
                                )
                            } else {
                                SpanStyle(
                                    color = Color.Blue,
                                    textDecoration = TextDecoration.Underline
                                )
                            }
                        },
                        onClick = {
                            println("Hashtag ${it.matchedText} clicked")
                        }
                    ),
                )
            )

            AutoLinkText(
                text = "This is very important",
                style = LocalTextStyle.current.copy(
                    color = LocalContentColor.current,
                ),
                textRules = listOf(
                    TextRule(
                        textMatcher = TextMatcher.StringMatcher("important"),
                        matchStyle = SpanStyle(color = Color.Red),
                    ),
                )
            )

            AutoLinkText(
                text = "Make every  other  word blue",
                style = LocalTextStyle.current.copy(
                    color = LocalContentColor.current,
                ),
                textRules = listOf(
                    TextRule(
                        textMatcher = TextMatcher.FunctionMatcher {
                            val matches = mutableListOf<SimpleTextMatchResult<Nothing?>>()
                            var currentWordStart = 0
                            "$it ".forEachIndexed { index, char ->
                                if (char.isWhitespace()) {
                                    val match = SimpleTextMatchResult(
                                        start = currentWordStart,
                                        end = index,
                                    )
                                    if (it.slice(match).isNotBlank()) {
                                        matches.add(match)
                                    }
                                    currentWordStart = index + 1
                                }
                            }
                            matches.filterIndexed { index, _ ->  index % 2 == 0 }
                        },
                        matchStyle = SpanStyle(color = Color.Blue),
                    ),
                )
            )

            var clickCount by remember { mutableIntStateOf(0) }

            AutoLinkText(
                text = "Make this clickable, this too but not THIS. Click count: $clickCount.",
                style = LocalTextStyle.current.copy(
                    color = LocalContentColor.current,
                ),
                textRules = listOf(
                    TextRule(
                        textMatcher = TextMatcher.StringMatcher("this"),
                        onClick = {
                            clickCount++
                        }
                    )
                )
            )
        }
    }
}