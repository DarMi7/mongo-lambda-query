package com.darmi.plugin.core;


import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author darmi
 */
public class LambdaUtils {

    public static <T> String getField(SFunction<T, ?> fn) {
        if (fn == null) return null;
        return convertToFieldName(fn);
    }

    /**
     * get filed name
     */
    private static <T> String convertToFieldName(SFunction<T, ?> fn) {
      SerializedLambda lambda = null;
      try {
        lambda = getSerializedLambda(fn);
      } catch (Exception e) {
        e.printStackTrace();
      }
      String methodName = lambda.getImplMethodName();
        String prefix = null;
        if (methodName.startsWith("get")) {
            prefix = "get";
        } else if (methodName.startsWith("is")) {
            prefix = "is";
        }
        if (prefix == null) {
            throw new RuntimeException("LambdaUtils ERROR");
        }

        return toLowerCaseFirstOne(methodName.replace(prefix, ""));
    }

    /**
     * get Serialized Lambda
     */
    private static SerializedLambda getSerializedLambda(Serializable fn) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = fn.getClass().getDeclaredMethod("writeReplace");
        method.setAccessible(Boolean.TRUE);
        return (SerializedLambda) method.invoke(fn);
    }

    /**
     * toLowerCaseFirstOne
     */
    private static String toLowerCaseFirstOne(String field) {
        if (Character.isLowerCase(field.charAt(0))) return field;
        else {
            char firstOne = Character.toLowerCase(field.charAt(0));
            String other = field.substring(1);
            return firstOne + other;
        }
    }
}
