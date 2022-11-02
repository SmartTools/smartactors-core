package info.smart_tools.smartactors.devtools.common.feature

import com.intellij.json.psi.JsonArray
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonValue

fun Feature.parseFeatureName(): FeatureName {
    val splitFeature = featureName.split(":")

    return FeatureName(
        groupId = splitFeature[0],
        name = splitFeature[1],
        version = splitFeature.getOrNull(2))
}

fun JsonFile.parseFeature(): Feature {
    val root = this.topLevelValue as JsonObject?

    val featureName = root?.findProperty("featureName")?.value?.text?.replace("\"", "") ?: ""
    val afterFeatures = root?.findProperty("afterFeatures")?.value as JsonArray?
    val repositoryData = root?.findProperty("repository")?.value as JsonObject?
    val plugins = root?.findProperty("plugins")?.value as JsonArray?

    val repositoryId = repositoryData?.findProperty("id")?.value?.text?.replace("\"", "") ?: ""
    val repositoryUrl = repositoryData?.findProperty("url")?.value?.text?.replace("\"", "") ?: ""

    val repository = Repository(repositoryId, repositoryUrl)

    return Feature(
        featureName = featureName,
        afterFeatures = afterFeatures?.valueList?.map(::valueToString) ?: listOf(),
        repository = repository,
        plugins = plugins?.valueList?.map(::valueToString) ?: listOf(),
    )
}

private fun valueToString(value: JsonValue) : String {
    return value.text.replace("\"", "")
}
