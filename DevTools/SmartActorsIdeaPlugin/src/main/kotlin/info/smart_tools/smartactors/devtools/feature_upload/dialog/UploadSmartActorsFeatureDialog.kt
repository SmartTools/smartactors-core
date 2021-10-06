package info.smart_tools.smartactors.devtools.feature_upload.dialog

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComponentValidator
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.DocumentAdapter
import com.intellij.util.ui.FormBuilder
import info.smart_tools.smartactors.devtools.common.dialog.ValidatorData
import info.smart_tools.smartactors.devtools.common.feature.Feature
import info.smart_tools.smartactors.devtools.common.feature.Repository
import info.smart_tools.smartactors.devtools.feature_upload.tool.upload
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JPasswordField
import javax.swing.JTextField
import javax.swing.event.DocumentEvent

class UploadSmartActorsFeatureDialog(
    private val project: Project,
    private val feature: Feature,
    private val actionPath: String
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
        usernameTextField = buildTextField()
        passwordTextField = buildTextField(password = true)
        repositoryIdTextField = buildTextField(placeholder = repository.id)
        repositoryUrlTextField = buildTextField(placeholder = repository.url)

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
        upload(
            featurePath = actionPath,
            username = usernameTextField.text,
            password = passwordTextField.text,
            repository = Repository(repositoryIdTextField.text, repositoryUrlTextField.text)
        )
        super.doOKAction()
    }

    private fun buildTextField(
        placeholder: String = "",
        password: Boolean = false,
        validatorData: ValidatorData? = null
    ): JTextField {
        val textField = if (!password) JTextField(placeholder) else JPasswordField(placeholder)
        textField.preferredSize = Dimension(300, 30)

        if (validatorData != null) {
            val validator: () -> ValidationInfo? = {
                val textInput = textField.text
                if (StringUtil.isNotEmpty(textInput)) {
                    if (!textInput.matches(validatorData.regex)) {
                        ValidationInfo(validatorData.errorMessage, textField)
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
            ComponentValidator(project).withValidator(validator).andStartOnFocusLost().installOn(textField)

            textField.document.addDocumentListener(object : DocumentAdapter() {
                override fun textChanged(e: DocumentEvent) {
                    ComponentValidator.getInstance(textField).ifPresent {
                        it.revalidate()
                    }
                }
            })
        }

        return textField
    }
}
