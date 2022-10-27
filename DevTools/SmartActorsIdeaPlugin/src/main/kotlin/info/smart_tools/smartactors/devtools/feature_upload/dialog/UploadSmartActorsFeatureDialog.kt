package info.smart_tools.smartactors.devtools.feature_upload.dialog

import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.util.ui.FormBuilder
import info.smart_tools.smartactors.devtools.common.dialog.TextFieldBuilder
import info.smart_tools.smartactors.devtools.common.feature.Feature
import info.smart_tools.smartactors.devtools.common.feature.Repository
import info.smart_tools.smartactors.devtools.common.feature.parseFeatureName
import info.smart_tools.smartactors.devtools.common.model.Credentials
import info.smart_tools.smartactors.devtools.common.persistence.createCredentialAttributes
import info.smart_tools.smartactors.devtools.common.persistence.toPersistence
import info.smart_tools.smartactors.devtools.feature_upload.task.UploadFeatureTask
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JTextField

class UploadSmartActorsFeatureDialog(
    private val project: Project,
    private val feature: Feature,
    private val featurePath: String
) : DialogWrapper(true) {

    private lateinit var usernameTextField: JTextField
    private lateinit var passwordTextField: JTextField
    private lateinit var repositoryIdTextField: JTextField
    private lateinit var repositoryUrlTextField: JTextField

    private var saveCredentials: Boolean = false
    private var loadedCredentials: Boolean = false

    init {
        init()
    }

    override fun createCenterPanel(): JComponent {
        setResizable(false)

        title = "Upload Feature"

        val repository = feature.repository
        val credentials = loadCredentials(repository.url)

        usernameTextField = TextFieldBuilder().newTextField(initialValue = credentials.username).build()
        passwordTextField = TextFieldBuilder().newTextField(initialValue = credentials.password, password = true).build()

        repositoryIdTextField = TextFieldBuilder().newTextField(initialValue = repository.id).build()
        repositoryUrlTextField = TextFieldBuilder().newTextField(initialValue = repository.url).build()

        val saveCredentialsCheckbox = JCheckBox(
            "${if (loadedCredentials) "Update" else "Save"} credentials for this repository",
            saveCredentials
        )
        saveCredentialsCheckbox.addItemListener {
            saveCredentials = it.stateChange == 1
        }

        return FormBuilder.createFormBuilder()
            .addLabeledComponent("Username: ", usernameTextField)
            .addLabeledComponent("Password: ", passwordTextField)
            .addComponentToRightColumn(saveCredentialsCheckbox)
            .addSeparator()
            .addLabeledComponent("Repository ID: ", repositoryIdTextField)
            .addLabeledComponent("Repository URL: ", repositoryUrlTextField)
            .panel
    }

    override fun doOKAction() {
        val credentials = Credentials(usernameTextField.text, passwordTextField.text)
        val repository = Repository(repositoryIdTextField.text, repositoryUrlTextField.text)

        if (saveCredentials) {
            storeCredentials(credentials, repository.url)
        }
        UploadFeatureTask(project, feature.parseFeatureName(), credentials, repository, featurePath).queue()

        super.doOKAction()
    }

    private fun storeCredentials(credentials: Credentials, repositoryUrl: String) {
        val attributes = createCredentialAttributes(repositoryUrl)
        val persistenceCredentials = credentials.toPersistence()
        PasswordSafe.instance.set(attributes, persistenceCredentials)
    }

    private fun loadCredentials(repositoryUrl: String): Credentials {
        val attributes = createCredentialAttributes(repositoryUrl)
        val persistenceCredentials = PasswordSafe.instance.get(attributes)

        val username = persistenceCredentials?.userName ?: ""
        val password = persistenceCredentials?.getPasswordAsString() ?: ""

        if (username != "" && password != "") {
            loadedCredentials = true
        }

        return Credentials(username, password)
    }
}
