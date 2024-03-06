package sh.calvin.autolinktext

// from https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/text/util/Linkify.java;l=209;drc=4f9480b13d3cab52255608ac5913922ca4269ac5
/**
 * Examines the character span matched by the pattern and determines
 * if the match should be turned into an actionable link.
 *
 * For example:  when matching web URLs you would like things like
 * http://www.example.com to match, as well as just example.com itself.
 * However, you would not want to match against the domain in
 * support@example.com.  So, when matching against a web URL pattern you
 * might also include a MatchFilter that disallows the match if it is
 * immediately preceded by an at-sign (@).
 *
 * @return         Whether this match should be turned into a link
 */
typealias MatchFilter<T> = (fullText: String, match: SimpleTextMatchResult<T>) -> Boolean

object MatchFilterDefaults {
    val NoOp: MatchFilter<Any?> = { _, _ -> true }

    // from https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/text/util/Linkify.java;l=151;drc=4f9480b13d3cab52255608ac5913922ca4269ac5
    /**
     *  Filters out web URL matches that occur after an at-sign (@).  This is
     *  to prevent turning the domain name in an email address into a web link.
     */
    val WebUrls: MatchFilter<Any?> =
        fun(fullText: String, match: SimpleTextMatchResult<*>): Boolean {
            if (match.start == 0) {
                return true
            }

            if (fullText[match.start - 1] == '@') {
                return false
            }

            if (fullText.slice(match).contains("@")) {
                return false
            }

            return true
        }

    private const val PHONE_NUMBER_MINIMUM_DIGITS = 5

    // from https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/text/util/Linkify.java;l=169;drc=4f9480b13d3cab52255608ac5913922ca4269ac5
    /**
     *  Filters out URL matches that don't have enough digits to be a
     *  phone number.
     */
    val PhoneNumber: MatchFilter<Any?> =
        fun(fullText: String, match: SimpleTextMatchResult<*>): Boolean {
            var digitCount = 0

            for (i in match.start until match.endExclusive) {
                if (fullText[i].isDigit()) {
                    digitCount++
                    if (digitCount >= PHONE_NUMBER_MINIMUM_DIGITS) {
                        return true
                    }
                }
            }
            return false
        }
}
