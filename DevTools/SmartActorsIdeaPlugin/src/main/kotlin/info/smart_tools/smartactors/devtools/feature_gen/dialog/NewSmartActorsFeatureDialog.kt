package info.smart_tools.smartactors.devtools.feature_gen.dialog

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComponentValidator
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.DocumentAdapter
import com.intellij.util.ui.FormBuilder
import info.smart_tools.smartactors.devtools.common.dialog.ValidatorData
import info.smart_tools.smartactors.devtools.common.feature.FeatureName
import info.smart_tools.smartactors.devtools.feature_gen.task.CreateFeatureTask
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JTextField
import javax.swing.event.DocumentEvent

class NewSmartActorsFeatureDialog(val project: Project, val actionPath: String) : DialogWrapper(true) {
    private val javaPackageRegexp = "^[a-z][a-z0-9_]*(\\.[a-z0-9_]+)+[0-9a-z_]\$".toRegex()
    private val featureNameRegexp = "^[a-zA-Z]+_[a-zA-Z]+\$".toRegex()

    private val groupIdErrorMessage = "Group ID must comply to Java package naming convention"
    private val featureNameErrorMessage = "Feature name may only contain characters and underscore"

    private lateinit var groupIdTextField: JTextField
    private lateinit var featureNameTextField: JTextField
    private lateinit var versionTextField: JTextField

    init {
        title = "New SmartActors Feature"
        init()
    }

    override fun createCenterPanel(): JComponent {
        setResizable(false)

        groupIdTextField = buildTextField(
            validatorData = ValidatorData(javaPackageRegexp, groupIdErrorMessage)
        )
        featureNameTextField = buildTextField(
            validatorData = ValidatorData(featureNameRegexp, featureNameErrorMessage)
        )
        versionTextField = buildTextField(
            placeholder = "0.1.0"
        )

        return FormBuilder()
            .addLabeledComponent("Group ID: ", groupIdTextField)
            .addLabeledComponent("Feature name: ", featureNameTextField)
            .addLabeledComponent("Version", versionTextField)
            .panel
    }

    override fun doOKAction() {
        CreateFeatureTask(
            ideaProject = project,
            featureName = getFeatureName(),
            path = actionPath
        ).queue()
        super.doOKAction()
    }

    private fun getFeatureName(): FeatureName {
        return FeatureName(
            groupId = groupIdTextField.text,
            name = featureNameTextField.text,
            version = versionTextField.text
        )
    }

    private fun buildTextField(
        placeholder: String = "",
        validatorData: ValidatorData? = null
    ): JTextField {
        val textField = JTextField(placeholder)
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
