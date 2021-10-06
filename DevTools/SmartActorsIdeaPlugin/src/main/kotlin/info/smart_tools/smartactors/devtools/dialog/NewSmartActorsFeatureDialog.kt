package info.smart_tools.smartactors.devtools.dialog

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComponentValidator
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.DocumentAdapter
import com.intellij.util.ui.FormBuilder
import info.smart_tools.smartactors.devtools.utilities.feature_gen.createFeature
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*
import javax.swing.event.DocumentEvent

class NewSmartActorsFeatureDialog(val project: Project, val actionPath: String) : DialogWrapper(true) {
    private val javaPackageRegexp = "^[a-z][a-z0-9_]*(\\.[a-z0-9_]+)+[0-9a-z_]\$".toRegex()
    private val featureNameRegexp = "^[a-zA-Z]+_[a-zA-Z]+\$".toRegex()

    private val groupIdErrorMessage = "Group ID must comply to Java package naming convention"
    private val featureNameErrorMessage = "Feature name may only contain characters and underscore"

    private lateinit var groupIdTextField: JTextField
    private lateinit var featureNameTextField: JTextField
    private lateinit var versionTextField: JTextField

    private lateinit var loadingPopup: JBPopup

    init {
        title = "New SmartActors Feature"
        init()
    }

    override fun createCenterPanel(): JComponent {
        setResizable(false)
        val popupFactory = JBPopupFactory.getInstance()

        loadingPopup = popupFactory.createComponentPopupBuilder(createLoadingThingy(), null)
            .setMovable(true)
            .setProject(project)
            .createPopup()
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
        loadingPopup.showCenteredInCurrentWindow(project)
        createFeature(
            groupId = groupIdTextField.text,
            featureName = featureNameTextField.text,
            version = versionTextField.text.orEmpty(),
            path = actionPath
        )
        loadingPopup.cancel()
        super.doOKAction()
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

    private fun createLoadingThingy(): JComponent {
        val layout = GridLayout(2, 1)
        layout.vgap = 10
        val panel = JPanel(layout)

        val label = JLabel("Creating new SmartActors feature...")
        val progressBar = JProgressBar()
        progressBar.preferredSize = Dimension(350, 5)
        progressBar.isIndeterminate = true

        panel.add(label)
        panel.add(progressBar)

        return panel
    }
}
