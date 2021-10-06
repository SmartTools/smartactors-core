package info.smart_tools.smartactors.devtools.utilities.feature_gen

import info.smart_tools.smartactors.feature_generator.FeatureGenerator
import kotlin.system.measureTimeMillis

fun createFeature(groupId: String, featureName: String, version: String?, path: String) {
    println("Creating feature $featureName")
    val elapsed = measureTimeMillis {
        val arguments = Arguments(groupId, featureName, version, path)
        FeatureGenerator.main(arguments.toArray())
        println("Created feature $featureName")
    }
    println("Feature was created in $elapsed ms.")
}
