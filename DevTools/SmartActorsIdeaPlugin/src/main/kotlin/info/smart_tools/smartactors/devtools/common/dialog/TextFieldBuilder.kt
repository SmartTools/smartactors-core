package info.smart_tools.smartactors.devtools.common.dialog

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComponentValidator
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.DocumentAdapter
import java.awt.Dimension
import javax.swing.JPasswordField
import javax.swing.JTextField
import javax.swing.event.DocumentEvent

class TextFieldBuilder {

    private lateinit var textField: JTextField

    fun newTextField(initialValue: String = "", password: Boolean = false): TextFieldBuilder {
        textField = if (!password) JTextField(initialValue) else JPasswordField(initialValue)
        textField.preferredSize = Dimension(300, 30)

        return this
    }

    fun addValidation(validatorData: ValidatorData, ideaProject: Project): TextFieldBuilder {
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
        ComponentValidator(ideaProject).withValidator(validator).andStartOnFocusLost().installOn(textField)

        textField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                ComponentValidator.getInstance(textField).ifPresent {
                    it.revalidate()
                }
            }
        })

        return this
    }

    fun setPreferredSize(dimension: Dimension): TextFieldBuilder {
        textField.preferredSize = dimension

        return this
    }

    fun build(): JTextField {
        return textField
    }
}
