package info.smart_tools.smartactors.devtools.feature_upload.dialog

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.util.ui.FormBuilder
import info.smart_tools.smartactors.devtools.common.dialog.TextFieldBuilder
import info.smart_tools.smartactors.devtools.common.feature.Feature
import info.smart_tools.smartactors.devtools.common.feature.Repository
import info.smart_tools.smartactors.devtools.common.feature.parseFeatureName
import info.smart_tools.smartactors.devtools.common.model.Credentials
import info.smart_tools.smartactors.devtools.feature_upload.task.UploadFeatureTask
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

    init {
        init()
    }

    override fun createCenterPanel(): JComponent {
        setResizable(false)

        title = "Upload Feature"

        val repository = feature.repository
        usernameTextField = TextFieldBuilder().newTextField().build()
        passwordTextField = TextFieldBuilder().newTextField(password = true).build()
        repositoryIdTextField = TextFieldBuilder().newTextField(initialValue = repository.id).build()
        repositoryUrlTextField = TextFieldBuilder().newTextField(initialValue = repository.url).build()

        return FormBuilder.createFormBuilder()
            .addLabeledComponent("Username: ", usernameTextField)
            .addLabeledComponent("Password: ", passwordTextField)
//            .addComponentToRightColumn(JCheckBox("Save credentials"))
            .addSeparator()
            .addLabeledComponent("Repository ID: ", repositoryIdTextField)
            .addLabeledComponent("Repository URL: ", repositoryUrlTextField)
            .panel
    }

    override fun doOKAction() {
        val credentials = Credentials(usernameTextField.text, passwordTextField.text)
        val repository = Repository(repositoryIdTextField.text, repositoryUrlTextField.text)

        UploadFeatureTask(project, feature.parseFeatureName(), credentials, repository, featurePath).queue()

        super.doOKAction()
    }
}
