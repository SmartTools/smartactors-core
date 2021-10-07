package info.smart_tools.smartactors.devtools.common.feature

data class Repository(val id: String, val url: String)

data class FeatureName(
    val groupId: String,
    val name: String,
    val version: String?
) {
    override fun toString(): String {
        return "$groupId:$name${if (version != null) ":$version" else null}"
    }
}

data class Feature(
    val featureName: String,
    val afterFeatures: List<String> = listOf(),
    val repository: Repository,
    val plugins: List<String> = listOf()
)
