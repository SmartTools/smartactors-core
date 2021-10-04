package info.smart_tools.smartactors.devtools.feature_upload

import info.smart_tools.smartactors.devtools.feature.Repository
import info.smart_tools.smartactors.uploader.Uploader

fun upload(
    featurePath: String,
    username: String,
    password: String,
    repository: Repository
): Boolean {
    return try {
        val arguments = Arguments(featurePath, username, password, repository.id, repository.url)
        Uploader.main(arguments.toArray())
        true
    } catch (e: Exception) {
        false
    }
}
