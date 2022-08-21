package machine;
import auxiliary.Alphabet;
import auxiliary.Message;
import machine.parts.Plugboard;
import machine.parts.Reflector;
import machine.parts.Rotor;
import machine.parts.Rotors;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnigmaMachine implements Enigma {

    private final Alphabet abc;
    private final Plugboard pb;
    private final Rotors rotors;
    private final Reflector reflector;

    private int messageCount;


    public EnigmaMachine(List<Rotor> rotors, Reflector r, Plugboard plugs, Alphabet alphabet) {
        this.abc = alphabet;
        this.rotors = new Rotors(rotors);
        this.pb = plugs;
        this.reflector = r;
        this.messageCount = 0;
    }


    @Override
    public Message processText(String text) {
        StringBuilder encodedMsg = new StringBuilder(text.length());
        String processed;
        char encoded;
        long start = System.nanoTime();
        for (char letter : text.toCharArray()) {
            letter = pb.plugboardConverter(letter);  // check in plugboard and swap letter if necessary
            int signal = rotors.forwardPass(letter, abc);
            //System.out.print("reflector:"+ abc.getLetter(signal) + "->");
            signal = reflector.getReflection(signal);
            //System.out.println(abc.getLetter(signal));
            letter = abc.getLetter(signal);
            int output = rotors.backwardPass(letter, abc);
            char outputLetter = abc.getLetter(output);
            encoded = pb.plugboardConverter(outputLetter);
            encodedMsg.append(encoded);
        }
        long finish = System.nanoTime();
        processed = encodedMsg.toString();
        this.messageCount++;
        return new Message(text, processed, finish - start);
    }

    @Override
    public void resetMachine() {
        this.rotors.resetRotors();
    }


    @Override
    public List<Integer> getRotorIDs() {
        List<Integer> ids = new ArrayList<>();
        Iterator<Rotor> it = rotors.getIterator();
        while (it.hasNext()) {
            ids.add(it.next().getID());
        }
        return ids;
    }

    @Override
    public List<Character> getRotorPositions() {
        List<Character> positions = new ArrayList<>();
        Iterator<Rotor> it = rotors.getReversedIterator();
        while (it.hasNext()) {
            int position = it.next().getRotorPosition();
            positions.add(abc.getLetter(position));
        }

        return positions;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(rotors.toString());
        sb.append(reflector.toString());
        sb.append(pb.toString());
        return sb.toString();
    }


    @Override
    public Reflector getReflector() {
        return new Reflector(this.reflector);
    }

    @Override
    public int getNumOfRotors() {
        return this.rotors.size();
    }


    @Override
    public Plugboard getPlugboard() {
        return new Plugboard(this.pb);
    }

    @Override
    public String getCurrentConfiguration() {
        StringBuilder sb = new StringBuilder();
        Iterator<Rotor> it = rotors.getReversedIterator();
        sb.append('<').append(it.next().getID());
        while(it.hasNext()){
            sb.append(',');
            sb.append(it.next().getID());
        }
        sb.append('>');
        sb.append('<');
        it = rotors.getReversedIterator();
        while(it.hasNext()){
            Rotor rotor = it.next();
            sb.append(rotor.getCurrentPosition());
            sb.append("(").append(rotor.getDistanceFromNotch()).append(")");
        }
        sb.append('>');

        sb.append(reflector.toString());
        sb.append(pb.toString());
        return sb.toString();
    }

    @Override
    public int getMessageCount() {
        return messageCount;
    }

}
