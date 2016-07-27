package info.smart_tools.smartactors.core.proof_of_assumption.perfomance_of_jackson;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by sevenbits on 7/25/16.
 */
public class PerfomanceOfJackson {

    private String json = "{\n" +
            "  \"wrapper\": {\n" +
            "    \"in_getIntValue\": [{\n" +
            "      \"name\": \"wds_getter_strategy\",\n" +
            "      \"args\": [\"message/IntValue\"]\n" +
            "    }],\n" +
            "    \"out_setIntValue\": [\n" +
            "      [{\n" +
            "        \"name\": \"wds_target_strategy\",\n" +
            "        \"args\": [\"local/value\", \"response/IntValue\"]\n" +
            "      }]\n" +
            "    ],\n" +
            "    \"in_getStringValue\": [{\n" +
            "      \"name\": \"wds_getter_strategy\",\n" +
            "      \"args\": [\"message/StringValue\"]\n" +
            "    }],\n" +
            "    \"out_setStringValue\": [\n" +
            "      [{\n" +
            "        \"name\": \"wds_target_strategy\",\n" +
            "        \"args\": [\"local/value\", \"response/StringValue\"]\n" +
            "      }]\n" +
            "    ],\n" +
            "    \"in_getListOfInt\": [{\n" +
            "      \"name\": \"wds_getter_strategy\",\n" +
            "      \"args\": [\"message/ListOfInt\"]\n" +
            "    }],\n" +
            "    \"out_setListOfInt\": [\n" +
            "      [{\n" +
            "        \"name\": \"wds_target_strategy\",\n" +
            "        \"args\": [\"local/value\", \"response/ListOfInt\"]\n" +
            "      }]\n" +
            "    ],\n" +
            "    \"in_getListOfString\": [{\n" +
            "      \"name\": \"wds_getter_strategy\",\n" +
            "      \"args\": [\"message/ListOfString\"]\n" +
            "    }],\n" +
            "    \"out_setListOfString\": [\n" +
            "      [{\n" +
            "        \"name\": \"wds_target_strategy\",\n" +
            "        \"args\": [\"local/value\", \"response/ListOfString\"]\n" +
            "      }]\n" +
            "    ],\n" +
            "    \"in_getBoolValue\": [{\n" +
            "      \"name\": \"wds_getter_strategy\",\n" +
            "      \"args\": [\"context/BoolValue\"]\n" +
            "    }],\n" +
            "    \"out_setBoolValue\": [\n" +
            "      [{\n" +
            "        \"name\": \"wds_target_strategy\",\n" +
            "        \"args\": [\"local/value\", \"response/BoolValue\"]\n" +
            "      }]\n" +
            "    ],\n" +
            "    \"in_getIObject\": [{\n" +
            "      \"name\": \"wds_getter_strategy\",\n" +
            "      \"args\": [\"context/IObject\"]\n" +
            "    }],\n" +
            "    \"out_setIObject\": [\n" +
            "      [{\n" +
            "        \"name\": \"wds_target_strategy\",\n" +
            "        \"args\": [\"local/value\", \"response/IObject\"]\n" +
            "      }]\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    @Test
    public void checkThousandDeserializationOfDSObject() {
        long startTime;
        long endTime;
        // warming-up
        try {
            for (int i = 0; i < 100; ++i) {
                new DSObject(json);
            }
        } catch (Exception e) {
        }
        // test
        startTime = System.currentTimeMillis();
        try {
            for (int i = 0; i < 10000; ++i) {
                new DSObject(json);
            }
        } catch (Exception e) {
        }

        endTime = System.currentTimeMillis();
        System.out.printf("DSObject - 1000 deserialization: %s \n", (endTime - startTime));
    }

    @Test
    public void checkThousandDeserializationOfDSObjectWithRecursiveIObject() {
        long startTime;
        long endTime;
        // warming-up
        try {
            for (int i = 0; i < 100; ++i) {
                new DSObjectWithRecursiveIObject(json);
            }
        } catch (Exception e) {
        }
        // test
        startTime = System.currentTimeMillis();
        try {
            for (int i = 0; i < 10000; ++i) {
                new DSObjectWithRecursiveIObject(json);
            }
        } catch (Exception e) {
        }

        endTime = System.currentTimeMillis();
        System.out.printf("DSObject with recursive IObject - 1000 deserialization: %s \n", (endTime - startTime));
    }
}
