package machine;

import auxiliary.Alphabet;
import machine.parts.Plugboard;
import machine.parts.Reflector;
import machine.parts.Rotor;
import machine.parts.Rotors;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnigmaMachine implements Enigma{
    private Alphabet abc;
    private Plugboard pb;
    private Rotors rotors;
    private Reflector reflector;

    private boolean debug;



    public EnigmaMachine(List<Rotor> rotors, Reflector r, Plugboard plugs, Alphabet alphabet, boolean debugMode){
        this.debug = debugMode;
        this.abc = alphabet;
        this.rotors = new Rotors(rotors);
        this.pb = plugs;
        this.reflector = r;
    }




    @Override
    public String processText(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        char encoded;
        for(char letter : text.toCharArray()){
            letter = pb.plugboardConverter(letter);
            int input = abc.getOrder(letter);
            input = rotors.forwardPass(letter,abc);
            input = reflector.getReflection(input);
            int output = rotors.backwardPass(letter,abc);
            encoded = pb.plugboardConverter(abc.getLetter(input));
            sb.append(encoded);
        }

        return sb.toString();
    }

    @Override
    public void resetMachine() {



    }

    @Override
    public void printMachine() {

    }

    @Override
    public void printHistory() {

    }

    @Override
    public List<Integer> getRotorIDs() {
        List<Integer> ids = new ArrayList<>();
        Iterator<Rotor> it = rotors.getIterator();
        while(it.hasNext()){
            ids.add(it.next().getID());
        }
        return ids;
    }

    @Override
    public List<Character> getRotorPositions() {
        List<Character> positions = new ArrayList<>();
        Iterator<Rotor> it = rotors.getIterator();
        while(it.hasNext()){
            int position = it.next().getRotorPosition();
            positions.add(abc.getLetter(position));
        }

        return positions;
    }

    @Override
    public Reflector getReflector() {
        return new Reflector(this.reflector);
    }

    @Override
    public Plugboard getPlugboard() {
        return new Plugboard(this.pb);
    }
}
