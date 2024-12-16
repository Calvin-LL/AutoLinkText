package sh.calvin.autolinktext.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import sh.calvin.autolinktext.SimpleTextMatchResult
import sh.calvin.autolinktext.TextMatcher
import sh.calvin.autolinktext.TextRule
import sh.calvin.autolinktext.autoLinkText
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
            Text(
                AnnotatedString.autoLinkText(
                    """
                    |Visit https://www.google.com
                    |Visit www.google.com
                    |Email test@example.com
                    |Call 6045557890
                    |Call +1 (604) 555-7890
                    |Call 604-555-7890
                    """.trimMargin()
                )
            )

            Text(
                AnnotatedString.autoLinkText(
                    "Make your own rules like #hashtag and @mention",
                    textRules = listOf(
                        TextRule.Clickable(
                            textMatcher = TextMatcher.RegexMatcher(Regex("#\\w+")),
                            onClick = {
                                println("Hashtag ${it.matchedText} clicked")
                            },
                        ),
                        TextRule.Clickable(
                            textMatcher = TextMatcher.RegexMatcher(Regex("@\\w+")),
                            onClick = {
                                println("Mention ${it.matchedText} clicked")
                            },
                        )
                    )
                )
            )

            Text(
                AnnotatedString.autoLinkText(
                    "Style the same rule differently like #hashtag1 and #hashtag2",
                    textRules = listOf(
                        TextRule.Clickable(
                            textMatcher = TextMatcher.RegexMatcher(Regex("#\\w+")),
                            stylesProvider = {
                                val hashtag = it.matchedText
                                if (hashtag == "#hashtag1") {
                                    TextLinkStyles(
                                        SpanStyle(
                                            color = Color.Red,
                                            textDecoration = TextDecoration.Underline
                                        )
                                    )
                                } else {
                                    TextLinkStyles(
                                        SpanStyle(
                                            color = Color.Blue,
                                            textDecoration = TextDecoration.Underline
                                        )
                                    )
                                }
                            },
                            onClick = {
                                println("Hashtag ${it.matchedText} clicked")
                            },
                        ),
                    )
                )
            )

            Text(
                AnnotatedString.autoLinkText(
                    "This is very important",
                    textRules = listOf(
                        TextRule.Styleable(
                            textMatcher = TextMatcher.StringMatcher("important"),
                            style = SpanStyle(color = Color.Red),
                        )
                    ),
                )
            )

            Text(
                AnnotatedString.autoLinkText(
                    "Make every  other  word blue",
                    textRules = listOf(
                        TextRule.Styleable(
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
                                matches.filterIndexed { index, _ -> index % 2 == 0 }
                            },
                            style = SpanStyle(color = Color.Blue),
                        ),
                    ),
                )
            )

            var clickCount by remember { mutableIntStateOf(0) }

            Text(
                AnnotatedString.autoLinkText(
                    "Make this clickable, this too but not THIS. Click count: $clickCount.",
                    textRules = listOf(
                        TextRule.Clickable(
                            textMatcher = TextMatcher.StringMatcher("this"),
                            onClick = {
                                clickCount++
                            },
                        )
                    )
                )
            )
        }
    }
}
