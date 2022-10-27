package info.smart_tools.smartactors.devtools.feature_upload.tool

import info.smart_tools.smartactors.devtools.common.feature.Repository
import info.smart_tools.smartactors.devtools.common.model.Credentials
import info.smart_tools.smartactors.uploader.Uploader

fun upload(
    featurePath: String,
    credentials: Credentials,
    repository: Repository
): Boolean {
    return try {
        val arguments = Arguments(featurePath, credentials.username, credentials.password, repository.id, repository.url)
        Uploader.main(arguments.toArray())
        true
    } catch (e: Exception) {
        // TODO: do something better than simply printing the entire stack trace
        println(e.stackTrace)
        false
    }
}
