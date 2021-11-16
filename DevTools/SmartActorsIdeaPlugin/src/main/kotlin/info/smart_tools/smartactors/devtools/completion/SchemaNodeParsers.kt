package info.smart_tools.smartactors.devtools.completion

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import info.smart_tools.smartactors.devtools.common.State

typealias NodeParser = (ObjectNode, JsonNodeFactory) -> Unit

fun parseIocEnum(node: ObjectNode, jsonFactory: JsonNodeFactory) {
    val iocData = State.getIocData()
    val prefix = node.findValue("smartActors:iocPrefix").asText()
    // TODO: change to regexp instead of prefix
    val valuesForEnum = iocData.filter { it.key.contains(prefix) }
        .map { it.key.removePrefix(prefix) }
        .map { jsonFactory.textNode(it) }

    node.putArray("enum").addAll(valuesForEnum)
    node.remove("smartActors:type")
    node.remove("smartActors:iocPrefix")
}

fun parseIocStrategy(node: ObjectNode, jsonFactory: JsonNodeFactory) {
    val iocData = State.getIocData()
    val iocKeys = iocData.map { it.key }.map { jsonFactory.textNode(it) }

    node.putArray("enum").addAll(iocKeys)
    node.remove("smartActors:type")
}

fun parseChainNames(node: ObjectNode, jsonFactory: JsonNodeFactory) {
    val chainData = State.getChainData()
    val chainNames = chainData.map { jsonFactory.textNode(it.name) }

    node.putArray("enum").addAll(chainNames)
    node.remove("smartActors:type")
}
