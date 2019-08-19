package info.smart_tools.smartactors.das.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Scanner;

public final class JsonFile {

    private JsonFile() {
    }

    public static IObject load(final File file) {
        IObject data = null;
        try (Scanner scanner = new Scanner(file)) {
            data = new DSObject(scanner.useDelimiter("\\Z").next());
        } catch (IOException e) {
            System.out.println("Could not read file: ");
            System.err.println(e);
        } catch (InvalidArgumentException e) {
            System.out.println("Could not parse file data: ");
            System.err.println(e);
        }

        return data;
    }

    public static void save(final File file, final IObject info) {
        BufferedWriter writer = null;
        try {

            Field f = info.getClass().getDeclaredField("OBJECT_MAPPER"); //NoSuchFieldException
            f.setAccessible(true);
            ObjectMapper objectMapper = (ObjectMapper) f.get(info);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            String s = ((DSObject) info).serialize();

            writer = new BufferedWriter(new FileWriter(file));
            writer.write(s);
        } catch (SerializeException e) {
            System.out.println("Could not serialize data: ");
            System.err.println(e);
        } catch (IOException e) {
            System.out.println("Could not save data to the file: ");
            System.err.println(e);
        } catch (NoSuchFieldException e) {
            // TODO: Empty catch block
        } catch (IllegalAccessException e) {
            // TODO: Empty catch block
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                System.out.println("Could not close write buffer: ");
                System.err.println(e);
            }
        }
    }
}
