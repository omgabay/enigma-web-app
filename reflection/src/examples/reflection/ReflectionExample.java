package examples.reflection;

public class ReflectionExample {

    public static void main(String[] args) {
        SherlockHolmes sherlock = new SherlockHolmes();
        Object mysteriousClass = new MysteriousClass("Murder", 5);
        sherlock.inspectMystery(mysteriousClass);

    }
}
