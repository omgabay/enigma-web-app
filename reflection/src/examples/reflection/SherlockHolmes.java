package examples.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SherlockHolmes {

    public void inspectMystery(Object object) {
        Class clazz = object.getClass();

        Field[] fields = clazz.getDeclaredFields();
        System.out.println("Watson, I found " + fields.length + " fields:" + Arrays.toString(fields));

        for (Field field : fields) {
            if (!Modifier.isPublic(field.getModifiers())) {
                field.setAccessible(true);
            }
            Class<?> type = field.getType();
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            System.out.println("Watson, the field " + field.getName() + " is " + type + (isStatic ? " and it is static!" : ""));
            if (type == int.class) {
                try {
                    int value = field.getInt(isStatic ? null : object);
                    System.out.println("Watson, the original value was " + value + ", but since I know its type I doubled it to " + value * 2);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    System.out.println("Watson, the value of " + field.getName() + " is " + field.get(object).toString());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                };
            }
        }

        Method[] methods = clazz.getMethods(); //all methods (including inherited ones)
        methods = clazz.getDeclaredMethods(); //only from the given class
        System.out.println("Watson, I found " + methods.length + " methods:" + Arrays.toString(methods));


        for (Method method : methods) {
            Class<?> type = method.getReturnType();
            System.out.println("Watson, the method " + method.getName() + " returns " + type + (Modifier.isStatic(method.getModifiers()) ? " and it is static!" : ""));
            System.out.println("Watson, I'm going to run it. It requires " + method.getParameterTypes().length + " parameters");

            try {
                Object result = method.invoke(object, new Object[]{});
                System.out.println("Watson, the method returnd: " + result);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                System.out.println("Watson, I failed. Going to try again!");

                try {
                    method.invoke(object, 10, 20, "test");
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
        }


        //creating new instance using reflection

        Constructor[] constructors = clazz.getConstructors();
        System.out.println("Watson, I found " + constructors.length + " Constructor(s):" + Arrays.toString(constructors));
        try {
            Object newMystery = constructors[1].newInstance("2001: A Space Odyssey", 2001);

            System.out.println("Come Watson, there is a new mystery: " + newMystery);
        } catch (InstantiationException ex) {
            Logger.getLogger(SherlockHolmes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SherlockHolmes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SherlockHolmes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(SherlockHolmes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}