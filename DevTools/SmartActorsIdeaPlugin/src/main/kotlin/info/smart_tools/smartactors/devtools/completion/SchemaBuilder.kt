package info.smart_tools.smartactors.devtools.completion

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import info.smart_tools.smartactors.devtools.common.State

class SchemaBuilder {

    private val iocData = State.getIocData()
    private val mapper = ObjectMapper()

    private val nodeParsers = mapOf<String, NodeParser>(
        Pair("iocEnum", ::parseIocEnum),
        Pair("iocStrategy", ::parseIocStrategy),
        Pair("chainName", ::parseChainNames)
    )

    fun build(configs: List<ConfigData>): String {
        val jsonFactory = JsonNodeFactory.instance
        val rootObject = jsonFactory.objectNode().apply {
            put("\$schema", "http://json-schema.org/draft-07/schema")
            put("title", "SmartActors config schema")
            put("type", "object")
        }
        val properties = jsonFactory.objectNode()

        configs.filter { it.schema != null }.forEach { config ->
            val configNode = mapper.readTree(config.schema) as ObjectNode

            val nodesToModify = configNode.findParents("smartActors:type").map { it as ObjectNode }
            nodesToModify.forEach { nodeToModify ->
                val nodeType = nodeToModify.findValue("smartActors:type")

                val parser = nodeParsers[nodeType.asText()]
                if (parser != null) {
                    parser(nodeToModify, jsonFactory)
                }
            }

            properties.set<ObjectNode>(config.name, configNode)
        }

        rootObject.set<ObjectNode>("properties", properties)
        return rootObject.toString()
    }
}
