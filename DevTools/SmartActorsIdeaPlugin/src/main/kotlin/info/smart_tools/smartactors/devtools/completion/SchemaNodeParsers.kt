package info.smart_tools.smartactors.devtools.completion

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import info.smart_tools.smartactors.devtools.common.State

typealias NodeParser = (ObjectNode, JsonNodeFactory) -> Unit

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

fun parseIocEnum(node: ObjectNode, jsonFactory: JsonNodeFactory) {
    val iocData = State.getIocData()
    val pattern = node.findValue("smartActors:iocPattern").asText().toRegex()

    val valuesForEnum = iocData.filter { pattern.matches(it.key) }
        .map { pattern.matchEntire(it.key)!!.groupValues[1] }
        .map { jsonFactory.textNode(it) }

    node.putArray("enum").addAll(valuesForEnum)
    node.remove("smartActors:type")
    node.remove("smartActors:iocPattern")
}
