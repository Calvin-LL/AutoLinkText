package sh.calvin.autolinktext


@RequiresOptIn(
    message = "In Android, [ContextData] is extended by [AndroidContextData] to provide a [Context] object.\n" +
            "In other platforms, [ContextData] is a simple interface with no methods.\n" +
            "Calling this function on Android without [AndroidContextData] will throw an exception.",
    level = RequiresOptIn.Level.ERROR
)
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class NotForAndroid