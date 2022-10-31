// WRITTEN BY OMER GABAY 22.07.22
package reflection.api;
import java.lang.Class;
import java.lang.reflect.*;
import java.util.*;

public class MyInvestigator implements Investigator{
    private Class<?> template;
    private Object obj;
    public MyInvestigator(){}   // default empty ctor

    // given an instance of some class load its java class object
    public void load(Object anInstanceOfSomething){
        this.obj = anInstanceOfSomething;
        this.template = anInstanceOfSomething.getClass();
    }

    // get the total number of functions in this.instance class
    public int getTotalNumberOfMethods() {
        Method [] m = this.template.getDeclaredMethods();
        return m.length;
    }

    public int getTotalNumberOfConstructors() {
        Constructor<?> [] ctorList = this.template.getDeclaredConstructors();
        return ctorList.length;
    }

    public int getTotalNumberOfFields() {
        Field [] fields = this.template.getDeclaredFields();
        return fields.length;

    }

    public Set<String> getAllImplementedInterfaces() {
        Set<String> res = new HashSet<>();
        for(Class<?> c : template.getInterfaces()){
            res.add(c.getSimpleName());
        }
        return res;
    }

    public int getCountOfConstantFields() {
        int count = 0;
        for (Field f : this.template.getDeclaredFields()){
            if(Modifier.isFinal(f.getModifiers())){
                count++;
            }
        }
        return count;
    }

    public int getCountOfStaticMethods() {
        int count = 0;
        Method [] methods = this.template.getDeclaredMethods();
        for(Method m : methods){
            if(Modifier.isStatic(m.getModifiers()))
                count++;
        }
        return count;
    }

    public boolean isExtending() {
        return this.template.getSuperclass() != null;
    }

    public String getParentClassSimpleName() {
        return this.template.getSuperclass().getSimpleName();
    }

    public boolean isParentClassAbstract() {
        return Modifier.isAbstract(this.template.getSuperclass().getModifiers());
    }
    // check if you need simple name instead of name for field
    public Set<String> getNamesOfAllFieldsIncludingInheritanceChain() {
        Set<String> fields_wt_inheritance  = new HashSet<>();
        Class<?> c = this.template;
        while(c != null){
            for(Field f : c.getDeclaredFields())
                fields_wt_inheritance.add(f.getName());
            c = c.getSuperclass();
        }
        return fields_wt_inheritance ;
    }

    public int invokeMethodThatReturnsInt(String methodName, Object... args) {
        try {
            Method m = this.template.getMethod(methodName);
            return (int) m.invoke(this.obj,args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object createInstance(int numberOfArgs, Object... args) {
        Constructor<?> [] ctors = this.template.getConstructors();
        for(Constructor<?> ctor : ctors){
            if(ctor.getParameterCount() == numberOfArgs){
                try {
                    return ctor.newInstance(args);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    public Object elevateMethodAndInvoke(String name, Class<?>[] parametersTypes, Object... args) {
        Method m;
        try {
            m = this.template.getDeclaredMethod(name,parametersTypes);
            m.setAccessible(true);
            return m.invoke(this.obj,args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getInheritanceChain(String delimiter) {
        List<String> inheritance_chain = new ArrayList<>();
        Class<?> c = this.template;
        while(c != null){
            inheritance_chain.add(0,c.getSimpleName());
            c = c.getSuperclass();
        }
        return String.join(delimiter,inheritance_chain);
    }


}
