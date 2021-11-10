package info.smart_tools.smartactors.devtools.completion

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import info.smart_tools.smartactors.ioc_viewer.parser.ioc.ParsedIocValue

typealias NodeParser = (ObjectNode, List<ParsedIocValue>, JsonNodeFactory) -> Unit

fun parseIocEnum(node: ObjectNode, iocData: List<ParsedIocValue>, jsonFactory: JsonNodeFactory) {
    val prefix = node.findValue("smartActors:iocPrefix").asText()
    val valuesForEnum = iocData.filter { it.key.contains(prefix) }
        .map { it.key.removePrefix(prefix) }
        .map { jsonFactory.textNode(it) }

    node.putArray("enum").addAll(valuesForEnum)
    node.remove("smartActors:type")
    node.remove("smartActors:iocPrefix")
}

fun parseIocStrategy(node: ObjectNode, iocData: List<ParsedIocValue>, jsonFactory: JsonNodeFactory) {
    val iocKeys = iocData.map { it.key }.map { jsonFactory.textNode(it) }

    node.putArray("enum").addAll(iocKeys)
    node.remove("smartActors:type")
}
