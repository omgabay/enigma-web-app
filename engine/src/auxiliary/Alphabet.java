package auxiliary;
import java.io.Serializable;
import java.util.HashMap;


public class Alphabet implements Serializable {
    private final String abc;
    private final HashMap<Character, Integer> order;
    public Alphabet(String alphabet){
        abc = alphabet;
        order = new HashMap<>();
        int count = 0;
        for (char letter: abc.toCharArray()) {
            order.put(letter,count++);
        }
    }

    public int getOrder(char letter){
        if (!order.containsKey(letter)){
            return -1;
        }
        return order.get(letter);
    }

    public char getLetter(int index){
        return abc.charAt(index);
    }

    public boolean isLetter(char c){
        return order.containsKey(Character.toUpperCase(c));
    }
    public char [] getABC(){ return abc.toCharArray();}

    public int size() {return abc.length();}

    @Override
    public String toString(){
        return abc;
    }

}
