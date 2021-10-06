package info.smart_tools.smartactors.devtools.builder.tool

data class Arguments(
    val featurePath: String
) {
    fun toArray(): Array<out String> {
        val args = ArrayList<String>()
        args.add("-featurePath")
        args.add(featurePath)

        return args.toTypedArray()
    }
}
