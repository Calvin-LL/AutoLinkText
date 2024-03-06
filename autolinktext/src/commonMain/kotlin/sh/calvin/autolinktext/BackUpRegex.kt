package sh.calvin.autolinktext

/**
 * A [BackUpRegex] is used when a platform has no native support for matching particular types of text.
 */
internal object BackUpRegex {
    // from https://regexr.com/39nr7
    internal val WebUrl = Regex(
        "[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)"
    )

    // from https://html.spec.whatwg.org/multipage/input.html#valid-e-mail-address
    internal val Email = Regex(
        "[a-zA-Z0-9.!#\$%&'*+\\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*"
    )

    internal val PhoneNumber = Regex(
        "[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}"
    )
}