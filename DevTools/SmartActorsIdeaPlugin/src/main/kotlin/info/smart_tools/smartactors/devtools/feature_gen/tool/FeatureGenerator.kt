package info.smart_tools.smartactors.devtools.feature_gen.tool

import info.smart_tools.smartactors.feature_generator.FeatureGenerator
import kotlin.system.measureTimeMillis

fun createFeature(groupId: String, featureName: String, version: String?, path: String): Boolean {
    return try {
        val arguments = Arguments(groupId, featureName, version, path)
        FeatureGenerator.main(arguments.toArray())
        true
    } catch (e: Exception) {
        // TODO: do something better than simply printing the entire stack trace
        println(e.stackTrace)
        false
    }
}
