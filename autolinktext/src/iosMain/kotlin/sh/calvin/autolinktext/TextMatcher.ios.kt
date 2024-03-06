package sh.calvin.autolinktext

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Foundation.NSDataDetector
import platform.Foundation.NSMakeRange
import platform.Foundation.NSTextCheckingResult
import platform.Foundation.NSTextCheckingTypeLink
import platform.Foundation.NSTextCheckingTypePhoneNumber
import platform.Foundation.URL
import platform.Foundation.matchesInString

actual object TextMatcherDefaults {
    @OptIn(ExperimentalForeignApi::class)
    actual fun webUrl(contextData: ContextData): TextMatcher {
        return TextMatcher.FunctionMatcher { text ->
            val detector = NSDataDetector(types = NSTextCheckingTypeLink, error = null)
            val range = NSMakeRange(0u, text.length.toULong())
            val matches = detector.matchesInString(
                text,
                options = 0u,
                range = range
            ) as List<NSTextCheckingResult>

            matches.mapNotNull { match ->
                if (match.resultType == NSTextCheckingTypeLink && match.URL?.scheme != "mailto") {
                    match.range.useContents {
                        val start = location.toInt()
                        val end = start + length.toInt()
                        SimpleTextMatchResult.TextMatch(start, end)
                    }
                } else {
                    null
                }
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun emailAddress(contextData: ContextData): TextMatcher {
        return TextMatcher.FunctionMatcher { text ->
            val detector = NSDataDetector(types = NSTextCheckingTypeLink, error = null)
            val range = NSMakeRange(0u, text.length.toULong())
            val matches = detector.matchesInString(
                text,
                options = 0u,
                range = range
            ) as List<NSTextCheckingResult>

            matches.mapNotNull { match ->
                if (match.resultType == NSTextCheckingTypeLink && match.URL?.scheme == "mailto") {
                    match.range.useContents {
                        val start = location.toInt()
                        val end = start + length.toInt()
                        SimpleTextMatchResult.TextMatch(start, end)
                    }
                } else {
                    null
                }
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun phoneNumber(contextData: ContextData): TextMatcher {
        return TextMatcher.FunctionMatcher { text ->
            val detector = NSDataDetector(types = NSTextCheckingTypePhoneNumber, error = null)
            val range = NSMakeRange(0u, text.length.toULong())
            val matches = detector.matchesInString(
                text,
                options = 0u,
                range = range
            ) as List<NSTextCheckingResult>

            matches.mapNotNull { match ->
                if (match.resultType == NSTextCheckingTypePhoneNumber) {
                    match.range.useContents {
                        val start = location.toInt()
                        val end = start + length.toInt()
                        SimpleTextMatchResult.TextMatch(start, end)
                    }
                } else {
                    null
                }
            }
        }
    }

    @Composable
    actual fun webUrl(): TextMatcher = webUrl(NullContextData)

    @Composable
    actual fun emailAddress(): TextMatcher = emailAddress(NullContextData)

    @Composable
    actual fun phoneNumber(): TextMatcher = phoneNumber(NullContextData)
}