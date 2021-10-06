package info.smart_tools.smartactors.devtools.utilities.builder

import info.smart_tools.smartactors.builder.Builder

fun buildFeature(
    featurePath: String
): Boolean {
    return try {
        val arguments = Arguments(
            featurePath = featurePath
        )
        Builder.main(arguments.toArray())
        true
    } catch (e: Exception) {
        // TODO: need to do something better here than simply printing stack trace
        println(e.stackTrace)
        false
    }
}
