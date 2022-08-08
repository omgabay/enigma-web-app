package machine.parts;
import auxiliary.Alphabet;
import exceptions.EnigmaException;
import exceptions.InvalidConfigurationException;
import generated.CTEPositioning;
import generated.CTERotor;

import java.util.*;

public class Rotor {
    private final int id;
    private int initialPosition;
    private int rotorPosition;
    private int notch;  // index of the notch

    private final int rotorSize;
    private ArrayList<Integer> rotorMapToOutput;
    private ArrayList<Integer> rotorMapToInput;


    private ArrayList<Character> rightColumn;
    private ArrayList<Character> leftColumn;

    private Map<Character,Integer> letterToRight;
    private Map<Character, Integer> letterToLeft;




    public Rotor(CTERotor cteRotor, Alphabet abc) {
        this.notch = cteRotor.getNotch() - 1;
        this.id = cteRotor.getId();
        this.rotorSize = abc.size();
        this.rotorPosition = 0;
        leftColumn = new ArrayList<>();
        rightColumn = new ArrayList<>();
        letterToRight = new HashMap<>();
        letterToLeft = new HashMap<>();

        for (CTEPositioning pos : cteRotor.getCTEPositioning()) {
            char left = pos.getLeft().charAt(0);
            char right = pos.getRight().charAt(0);
            leftColumn.add(left);
            rightColumn.add(right);

        }
        for (char letter : leftColumn) {
            letterToRight.put(letter, rightColumn.indexOf(letter));
            letterToLeft.put(letter, leftColumn.indexOf(letter));

        }
    }



    public boolean Rotate(){
        rotorPosition++;
        rotorPosition %= rotorSize;
        leftColumn.add(leftColumn.remove(0));
        rightColumn.add(rightColumn.remove(0));
        return rotorPosition == this.notch;
    }


    public int getID(){
        return this.id;
    }
    public int getRotorPosition(){
        return this.rotorPosition;
    }
    public void validateRotor() throws EnigmaException{

        // Check if notch is not out of bounds
        if(this.notch < 0 || this.notch >= rotorSize){
            String msg = "Rotor #"+this.id + ": Notch is not defined within valid range: (1," + rotorSize + ")";
            throw new InvalidConfigurationException(msg);
        }
        // Check for rotor double mapping two inputs that map to the same exit
       /* Set<Integer> rotorMappings = new HashSet<>(rotorSize);
        for (int input = 0; input < this.rotorSize; input++) {
            int delta = rotorMapToOutput.get(input);
            int output = getRotorIndex(input+delta);
            if(rotorMappings.contains(output)){
                String msg = "Rotor #" + this.id + ": has double mapping to position " + output;
            }
            rotorMappings.add(output);
        }*/


    }
    private int getRotorIndex(int input){
        int output = input % rotorSize;
        if(output < 0){
            output = this.rotorSize - input;
        }
        return output;
    }

    /**
     * @param input The function gets the input letter and returns the index on the wheel where we exit
     * @return
     */
    public int getLeftToRightMapping(int input){
        char letter = leftColumn.get(input);
        int output = this.letterToRight.get(letter) - this.rotorPosition;
        if (output < 0){
            output = this.rotorSize + output;
        }
        return output;
    }

    public int getRightToLeftMapping(int input){
        char letter = rightColumn.get(input);   // gets the letter from the index in right column
        int output = this.letterToLeft.get(letter) - this.rotorPosition;
        if (output < 0){
            output = (this.rotorSize + output) % this.rotorSize;
        }
        return output;
    }


    public void setPosition(int position) {
        while(rotorPosition != position){
            leftColumn.add(leftColumn.remove(0));
            rightColumn.add(rightColumn.remove(0));
            rotorPosition++;
            rotorPosition %= rotorSize;
        }
    }
}
