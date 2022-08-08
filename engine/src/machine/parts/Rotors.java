package machine.parts;

import auxiliary.Alphabet;

import java.util.*;

public class Rotors {

    private LinkedList<Rotor> rotors;
    private List<Integer> initialConfig;



    public Rotors(List<Rotor> rotorsList) {
        initialConfig = new ArrayList<>();
        rotors = new LinkedList<>();
        rotors.addAll(rotorsList);

        for(Rotor r: rotors) {
            initialConfig.add(r.getRotorPosition());
        }

    }





    public int forwardPass(char input, Alphabet abc){
        StringBuilder sb = new StringBuilder();
        sb.append(input);// FOR DEBUG
        this.rotateFirstRotor();
        int index = abc.getOrder(input);
        char output = input;
        for (Rotor r : rotors) {
            sb.append("->"); // FOR DEBUG
             index = r.getRightToLeftMapping(index);
             output = abc.getLetter(index);
            sb.append(output);
        }
        sb.append('\n');
        System.out.print(sb);
        return abc.getOrder(output);
    }

    private void rotateFirstRotor() {
        Rotor rotor = this.rotors.getFirst();
        Iterator<Rotor> it = this.rotors.iterator();
        boolean keepRotate;
        do{
            keepRotate = it.next().Rotate();
        }while(it.hasNext() && keepRotate);
    }

    /**
     * @param input - the signal coming from the reflector
     * @return output signal after passing the rotors on the way back - reflector > rotors > plugboard > output
     */
    public int backwardPass(char input, Alphabet abc){
        int index = abc.getOrder(input);
        char output = input;
        StringBuilder sb = new StringBuilder();
        sb.append(input);// FOR DEBUG
        Iterator<Rotor> it = rotors.descendingIterator();
        while(it.hasNext()){
            sb.append("->"); // FOR DEBUG
            Rotor r = it.next();
            index = r.getLeftToRightMapping(index);
            output = abc.getLetter(index);
            sb.append(output);
        }
        sb.append('\n');
        System.out.print(sb);
        return abc.getOrder(output);

    }

    public Iterator<Rotor> getIterator(){
        return rotors.iterator();
    }



}
