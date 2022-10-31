package examples.dynamicproxy;

import java.lang.reflect.Proxy;

public class DynamicProxyExample {

    public static void main (String[] args) {
        int myMoney = 100;
        Banker banker = getMyBanker(new HonestBanker());
        System.out.println("Giving the banker my money: " + myMoney);
        banker.setMoney(myMoney);

        System.out.println("Getting my money from the banker: " + banker.getMoney());
    }

    private static Banker getMyBanker(Banker banker) {
        return (Banker) Proxy.newProxyInstance(
                banker.getClass().getClassLoader(),
                new Class[] {Banker.class},
                new RegulatoryInspector());
    }
}