package info.smart_tools.smartactors.devtools.feature

data class Repository(val id: String, val url: String)

data class Feature(
    val featureName: String,
    val afterFeatures: List<String> = listOf(),
    val repository: Repository,
    val plugins: List<String> = listOf()
)
