package sh.calvin.autolinktext

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Foundation.NSDataDetector
import platform.Foundation.NSMakeRange
import platform.Foundation.NSTextCheckingResult
import platform.Foundation.NSTextCheckingTypeLink
import platform.Foundation.NSTextCheckingTypePhoneNumber
import platform.Foundation.URL
import platform.Foundation.matchesInString

object IosTextMatcherDefaults : TextMatcherDefaultsInterface {
    @NotForAndroid
    @OptIn(ExperimentalForeignApi::class)
    override fun webUrl(contextData: ContextData): TextMatcher<Any?> {
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
                        SimpleTextMatchResult(start, end, match.URL)
                    }
                } else {
                    null
                }
            }
        }
    }

    @NotForAndroid
    @OptIn(ExperimentalForeignApi::class)
    override fun emailAddress(contextData: ContextData): TextMatcher<Any?> {
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
                        SimpleTextMatchResult(start, end, match.URL)
                    }
                } else {
                    null
                }
            }
        }
    }

    @NotForAndroid
    @OptIn(ExperimentalForeignApi::class)
    override fun phoneNumber(contextData: ContextData): TextMatcher<Any?> {
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
                        SimpleTextMatchResult(start, end, match.URL)
                    }
                } else {
                    null
                }
            }
        }
    }
}

actual fun getMatcherDefaults(): TextMatcherDefaultsInterface = IosTextMatcherDefaults