package sh.calvin.autolinktext

import android.util.Patterns

// from https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/text/util/Linkify.java;l=234;drc=4f9480b13d3cab52255608ac5913922ca4269ac5
/**
 * MatchTransformer enables client code to have more control over
 * how matched patterns are represented as URLs.
 *
 * For example:  when converting a phone number such as "(919)  555-1212"
 * into a link, the parentheses, white space, and hyphen can be
 * removed to produce 9195551212.
 */
fun interface MatchTransformer {
    /**
     * Examines the matched text and either passes it through or uses the
     * data in the Matcher state to produce a replacement.
     *
     * @param match    The regex matcher state that found this URL text
     * @param text     The full original text
     *
     * @return         The transformed form of the URL
     */
    fun transformLink(text: String, match: TextMatchResult): String
}

object MatchTransformerDefaults {
    val NoOp = MatchTransformer { text, match -> text.slice(match) }

    // from https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/text/util/Linkify.java;l=192;drc=4f9480b13d3cab52255608ac5913922ca4269ac5
    /**
     *  Transforms matched phone number text into something suitable
     *  to be used in a tel: URL.  It does this by removing everything
     *  but the digits and plus signs.  For instance:
     *  &apos;+1 (919) 555-1212&apos;
     *  becomes &apos;+19195551212&apos;
     */
    val PhoneNumbers = MatchTransformer { text, match ->
        when (match) {
            is TextMatchResult.MatcherMatch -> Patterns.digitsAndPlusOnly(match.matcher)
            is TextMatchResult.TextMatch -> NoOp.transformLink(text, match)
            is TextMatchResult.RegexMatch -> NoOp.transformLink(text, match)
        }
    }
}