package info.smart_tools.smartactors.devtools.common.persistence

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.generateServiceName
import info.smart_tools.smartactors.devtools.common.model.Credentials

typealias PersistenceCredentials = com.intellij.credentialStore.Credentials

fun createCredentialAttributes(key: String): CredentialAttributes {
    return CredentialAttributes(generateServiceName("SmartActorsDevTools", key))
}

fun Credentials.toPersistence(): PersistenceCredentials {
    return PersistenceCredentials(username, password)
}
