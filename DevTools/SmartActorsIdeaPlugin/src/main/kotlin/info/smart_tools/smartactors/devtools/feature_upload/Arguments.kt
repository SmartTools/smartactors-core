package info.smart_tools.smartactors.devtools.feature_upload

data class Arguments(
    val featurePath: String,
    val username: String,
    val password: String,
    val repositoryId: String? = null,
    val repositoryUrl: String? = null
) {
    fun toArray(): Array<out String> {
        val args = ArrayList<String>()
        args.add("-featurePath")
        args.add(featurePath)
        args.add("-username")
        args.add(username)
        args.add("-password")
        args.add(password)
        if (repositoryId != null && repositoryUrl != null) {
            args.add("-repositoryId")
            args.add(repositoryId)
            args.add("-repositoryUrl")
            args.add(repositoryUrl)
        }

        return args.toTypedArray()
    }
}
