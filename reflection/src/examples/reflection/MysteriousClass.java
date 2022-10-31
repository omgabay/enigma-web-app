package examples.reflection;

public class MysteriousClass {
    static int anigmaticSomething;

    private String mysteriousData;
    @Computerized
    private int unknownValue;

    public MysteriousClass(){}

    public MysteriousClass(String mysteriousData, int unknownValue) {
        this.mysteriousData = mysteriousData;
        this.unknownValue = unknownValue;
    }

//    @Deprecated 
    public void crackCode (int key, long seed, String code) {
        System.out.println("Cracking the code:" + code + " using the key: " + key);
        ++anigmaticSomething;
    }

    public static int getAnigmaticSomething() {
        return anigmaticSomething;
    }

    @Override
    public String toString() {
        return "MysteriousClass{" + "mysteriousData=" + mysteriousData + ", unknownValue=" + unknownValue + '}';
    }
}