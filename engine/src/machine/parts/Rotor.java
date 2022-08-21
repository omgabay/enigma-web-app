package machine.parts;
import auxiliary.Alphabet;
import exceptions.EnigmaException;
import exceptions.InvalidConfigurationException;
import generated.CTEPositioning;
import generated.CTERotor;
import java.io.Serializable;
import java.util.*;

public class Rotor implements Serializable {
    private final int id;
    private int rotorStartPosition;
    private int rotorPosition;
    private int notch;  // index of the notch

    private final int rotorSize;
    private ArrayList<Integer> rotorMapToOutput;
    private ArrayList<Integer> rotorMapToInput;

    private char rotorInitConfig;
    private ArrayList<Character> rightColumn;
    private ArrayList<Character> leftColumn;

    private Map<Character,Integer> letterToRight;
    private Map<Character, Integer> letterToLeft;




    public Rotor(CTERotor cteRotor, Alphabet abc) {
        this.notch = cteRotor.getNotch() - 1;
        this.id = cteRotor.getId();
        this.rotorSize = abc.size();
        this.rotorPosition = 0;
        this.rotorInitConfig = abc.getLetter(0);
        this.rotorStartPosition = 0;
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
    public void validateRotor(Alphabet abc) throws EnigmaException{

        // Check if notch is not out of bounds
        if(this.notch < 0 || this.notch >= rotorSize){
            String msg = "Rotor #"+this.id + ": Notch is not defined within valid range: (1," + rotorSize + ")";
            throw new InvalidConfigurationException(msg);
        }
        // Check for rotor double mapping two inputs that map to the same exit

        Set<Character> left = new HashSet<>();
        Set<Character> right = new HashSet<>();
        for (char letter :abc.getABC()) {
            left.add(letter);
            right.add(letter);
        }
        for (char letter : this.rightColumn) {
            if(!abc.isLetter(letter)){
                String msg = "Rotor #" + this.id + " Right column contains letter not in the alphabet (" + letter + ")";
                throw new InvalidConfigurationException(msg);
            }
            if(!right.contains(letter)){
                String msg = "Rotor #" + this.id + ": Rotor Right Column has extra mapping for the same letter (" + letter +")";
                throw new InvalidConfigurationException(msg);
            }
            right.remove(letter);
        }
        for (char letter : this.leftColumn) {
            if(!abc.isLetter(letter)){
                String msg = "Rotor #" + this.id + " Right column contains letter not in the alphabet (" + letter + ")";
                throw new InvalidConfigurationException(msg);
            }
            if(!left.contains(letter)){
                String msg = "Rotor #" + this.id + ": Rotor Left Column has extra mapping for the same letter (" + letter + ")";
                throw new InvalidConfigurationException(msg);
            }
            left.remove(letter);
        }
        if(!right.isEmpty()){
            char something = right.iterator().next();
            String msg = "Rotor #" + this.id + ": Right Column is missing mapping for letter " + something;
            throw new InvalidConfigurationException(msg);
        }
        if(!left.isEmpty()){
            char something = left.iterator().next();
            String msg = "Rotor #" + this.id + ": Left Column is missing mapping for letter " + something;
            throw new InvalidConfigurationException(msg);
        }


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


    public void setInitialPosition(char position) {
        this.rotorInitConfig = position;
        char curr = rightColumn.get(0);
        while(curr != position){
            leftColumn.add(leftColumn.remove(0));
            rightColumn.add(rightColumn.remove(0));
            rotorPosition++;
            rotorPosition %= rotorSize;
            curr = rightColumn.get(0);
        }
        this.rotorStartPosition = rotorPosition;
    }

    public void setPosition(int position){
        while(position != rotorPosition){
            leftColumn.add(leftColumn.remove(0));
            rightColumn.add(rightColumn.remove(0));
            rotorPosition++;
            rotorPosition %= rotorSize;
        }
    }



    public char getRotorInitConfig(){
        return this.rotorInitConfig;
    }

    public char getCurrentPosition(){
        return this.rightColumn.get(0);
    }

    public int getDistanceFromNotch() {
        if(this.notch >= this.rotorPosition) {
            return this.notch - this.rotorPosition;
        }
        return (this.rotorSize-this.rotorPosition) + 1 + this.notch;
    }

    public int getDistanceFromNotch(int position){
        if(this.notch >= position) {
            return this.notch - position;
        }
        return (this.rotorSize-position) + 1 + this.notch;
    }


    public String toString(){
        return this.rotorInitConfig+"("+getDistanceFromNotch(rotorStartPosition)+")";
    }


}
