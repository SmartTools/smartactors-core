package info.smart_tools.smartactors.devtools.feature_gen.dialog

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.util.ui.FormBuilder
import info.smart_tools.smartactors.devtools.common.dialog.TextFieldBuilder
import info.smart_tools.smartactors.devtools.common.dialog.ValidatorData
import info.smart_tools.smartactors.devtools.common.feature.FeatureName
import info.smart_tools.smartactors.devtools.feature_gen.task.CreateFeatureTask
import javax.swing.JComponent
import javax.swing.JTextField

class NewSmartActorsFeatureDialog(private val project: Project, private val actionPath: String) : DialogWrapper(true) {
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

        groupIdTextField = TextFieldBuilder()
            .newTextField()
            .addValidation(ValidatorData(javaPackageRegexp, groupIdErrorMessage), project)
            .build()
        featureNameTextField = TextFieldBuilder()
            .newTextField()
            .addValidation(ValidatorData(featureNameRegexp, featureNameErrorMessage), project)
            .build()
        versionTextField = TextFieldBuilder().newTextField(initialValue = "0.1.0").build()

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
}
