package info.smart_tools.smartactors.devtools.utilities.feature_gen

data class Arguments(
    val groupId: String,
    val featureName: String,
    val version: String? = null,
    val projectPath: String
) {
    fun toArray(): Array<out String> {
        val args = ArrayList<String>()
        args.add("-projectPath")
        args.add(projectPath)
        args.add("-groupId")
        args.add(groupId)
        args.add("-name")
        args.add(featureName)
        if (version != null && version != "") {
            args.add("-version")
            args.add(version)
        }

        return args.toTypedArray()
    }
}
