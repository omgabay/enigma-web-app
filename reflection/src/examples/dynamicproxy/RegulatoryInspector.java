package examples.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RegulatoryInspector implements InvocationHandler {

//    private Banker banker;

    public RegulatoryInspector () {
//        this.banker = new BadBanker()...
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (args != null) { // it's the set money method, but normally should check and verify it...
            System.out.println("Stole some money '" + args[0] + "'");
        } else { // it's the get money which does not get any parameters...
            System.out.println("method: " + method.getName() + " optimism... ch ch ch ch ch ch");
        }


//        Object result = method.invoke(banker, args); //simply runs the method that got invoked on the actual banker

//        System.out.println("Inspetor checked after method '" + method.getName() + "' was invoked");

        return 0;
//        return result;  //return the result to whatever class called the method
    }
}
