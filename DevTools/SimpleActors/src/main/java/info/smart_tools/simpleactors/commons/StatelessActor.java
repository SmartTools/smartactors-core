package info.smart_tools.simpleactors.commons;

import info.smart_tools.simpleactors.commons.annotations.Executable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class StatelessActor implements IActor {

    Map<String, Method> methods = Arrays.stream(this.getClass().getDeclaredMethods())
            .filter(m -> m.getDeclaredAnnotation(Executable.class) != null)
            .collect(Collectors.toMap(Method::getName, m -> m));

    public void execute(final String methodName, final IMessage message) {
        Method method = this.methods.get(methodName);
        MethodParameters methodParameters = message.get(CommonParameters.METHOD_PARAMS);
        Object result = null;
        try {
            result =
                    null != methodParameters.getArgumentPaths() ?
                            method
                            .invoke(
                                    this,
                                    methodParameters
                                            .getArgumentPaths()
                                            .stream()
                                            .map(
                                                    a -> getArgument(a, message)
                                            )
                                            .toArray()
                            ) :
                            method.invoke(this);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not execute method " + methodName + " of actor.",
                    e
            );
        }

        if (null != methodParameters.getResponsePath() && !methodParameters.getResponsePath().isEmpty()) {
            putResult(methodParameters.getResponsePath(), result, message);
        }
    }

    private Object getArgument(final String argumentPath, final IMessage message) {
        if (argumentPath.startsWith(CommonParameters.CONSTANT)) {
            String arg = argumentPath.replace(CommonParameters.CONSTANT, "");
            if (arg.startsWith(CommonParameters.CONSTANT_TO_BOOLEAN)) {
                return Boolean.valueOf(arg.replace(CommonParameters.CONSTANT_TO_BOOLEAN, ""));
            }
            if (arg.startsWith(CommonParameters.CONSTANT_TO_INT)) {
                return Integer.valueOf(arg.replace(CommonParameters.CONSTANT_TO_INT, ""));
            }
            if (arg.startsWith(CommonParameters.CONSTANT_TO_DOUBLE)) {
                return Double.valueOf(arg.replace(CommonParameters.CONSTANT_TO_DOUBLE, ""));
            }
            if (arg.startsWith(CommonParameters.CONSTANT_TO_STRING)) {
                return arg.replace(CommonParameters.CONSTANT_TO_STRING, "");
            }

            return arg;
        } else if (argumentPath.equals(CommonParameters.MESSAGE)) {
            return message;
        } else {
            return message.get(argumentPath);
        }
    }

    private void putResult(final String responsePath, final Object result, final IMessage message) {
        if (result instanceof Map && responsePath.equals(CommonParameters.SPLIT_RESPONSE)) {
            ((Map<?,?>) result).forEach(
                    (k, v) -> {
                        if (k instanceof String) {
                            message.put((String) k, v);
                        }
                    }
            );
        } else {
            message.put(responsePath, result);
        }
    }
}





