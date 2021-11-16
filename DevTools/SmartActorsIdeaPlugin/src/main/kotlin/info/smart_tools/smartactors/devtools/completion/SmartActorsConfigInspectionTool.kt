package info.smart_tools.smartactors.devtools.completion

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.json.psi.JsonArray
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonValue
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.jsonSchema.JsonSchemaMappingsProjectConfiguration
import com.jetbrains.jsonSchema.UserDefinedJsonSchemaConfiguration
import com.jetbrains.jsonSchema.impl.JsonSchemaVersion
import com.jetbrains.jsonSchema.impl.inspections.JsonSchemaComplianceInspection
import info.smart_tools.smartactors.devtools.common.State
import java.io.File

// TODO: it may not be an inspection tool, need to investigate
class SmartActorsConfigInspectionTool : LocalInspectionTool() {

    private val configManager = ConfigurationManager().getConfigSections()
    private val project = State.getProject()

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        val configuration = JsonSchemaMappingsProjectConfiguration.getInstance(project)

        if (!configuration.stateMap.keys.contains("SmartActors common schema")) {
            val result = SchemaBuilder().build(configManager)
            val tempSchema = File.createTempFile("smart_actors_common_schema", ".json")
            tempSchema.writeText(result)

            // TODO: possible null pointer
            val schemaFile = LocalFileSystem.getInstance().findFileByIoFile(tempSchema)!!

            // TODO baseDir is deprecated, need to find another solution
            val projectBaseDir = project.baseDir
            val virtualFile = holder.file.virtualFile

            var path = if (projectBaseDir == null) null else VfsUtilCore.getRelativePath(virtualFile, projectBaseDir)
            if (path == null) {
                path = virtualFile.url
            }

            val item = UserDefinedJsonSchemaConfiguration.Item(path, false, false)
            configuration.addConfiguration(
                UserDefinedJsonSchemaConfiguration(
                    "SmartActors common schema",
                    JsonSchemaVersion.SCHEMA_7,
                    schemaFile.path,
                    true, listOf(item)
                )
            )
        }

        return JsonSchemaComplianceInspection().buildVisitor(holder, isOnTheFly)
    }

    private fun validateObjects(objects: JsonArray, holder: ProblemsHolder) {
        // (objects.valueList[0].children[0] as JsonProperty).nameElement.text < field name will be acquired
        objects.valueList.forEach { validateObject(it, holder) }
    }

    private fun validateObject(jsonObject: JsonValue, holder: ProblemsHolder) {
        val mappedFields = jsonObject.children.map {
            val property = it as JsonProperty
            property.nameElement.getValue()
        }

        if (!mappedFields.contains("name")) {
            holder.registerProblem(jsonObject, "\"name\" property is missing.", ProblemHighlightType.GENERIC_ERROR)
        }
    }
}

fun JsonValue.getValue(): String {
    return text.subSequence(1, text.length - 1).toString()
}
