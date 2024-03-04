package sh.calvin.autolinktext


// from https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/text/util/Linkify.java;l=209;drc=4f9480b13d3cab52255608ac5913922ca4269ac5
/**
 *  MatchFilter enables client code to have more control over
 *  what is allowed to match and become a link, and what is not.
 *
 *  For example:  when matching web URLs you would like things like
 *  http://www.example.com to match, as well as just example.com itelf.
 *  However, you would not want to match against the domain in
 *  support@example.com.  So, when matching against a web URL pattern you
 *  might also include a MatchFilter that disallows the match if it is
 *  immediately preceded by an at-sign (@).
 */
fun interface MatchFilter {
    /**
     * Examines the character span matched by the pattern and determines
     * if the match should be turned into an actionable link.
     *
     * @param s        The body of text against which the pattern
     * was matched
     *
     * @return         Whether this match should be turned into a link
     */
    fun acceptMatch(s: String, match: TextMatchResult): Boolean
}

object MatchFilterDefaults {
    val NoOp = MatchFilter { _, _ -> true }

    // from https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/text/util/Linkify.java;l=151;drc=4f9480b13d3cab52255608ac5913922ca4269ac5
    /**
     *  Filters out web URL matches that occur after an at-sign (@).  This is
     *  to prevent turning the domain name in an email address into a web link.
     */
    val WebUrls = MatchFilter { s, match ->
        if (match.start == 0) {
            return@MatchFilter true
        }

        if (s[match.start - 1] == '@') {
            return@MatchFilter false
        }

        return@MatchFilter true
    }

    // from https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/text/util/Linkify.java;l=140;drc=4f9480b13d3cab52255608ac5913922ca4269ac5
    /**
     * Don't treat anything with fewer than this many digits as a
     * phone number.
     */
    private const val PHONE_NUMBER_MINIMUM_DIGITS = 5

    // from https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/text/util/Linkify.java;l=169;drc=4f9480b13d3cab52255608ac5913922ca4269ac5
    /**
     *  Filters out URL matches that don't have enough digits to be a
     *  phone number.
     */
    val PhoneNumbers = MatchFilter { s, match ->
        var digitCount = 0

        for (i in match.start until match.end) {
            if (Character.isDigit(s[i])) {
                digitCount++
                if (digitCount >= PHONE_NUMBER_MINIMUM_DIGITS) {
                    return@MatchFilter true
                }
            }
        }
        return@MatchFilter false
    }
}