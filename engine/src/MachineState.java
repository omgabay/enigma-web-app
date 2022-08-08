import auxiliary.Message;
import machine.parts.Plugboard;

import java.io.Serializable;
import java.util.List;

public class MachineState implements Serializable {
    private List<Integer> rotorIds;
    private List<Character> rotorPositions;
    private String reflectorID;
    private Plugboard plugboard;
    private List<Message> processedMessages;




    @Override
    public String toString(){
        String res = "<";
        int index;
        // adding rotorIds - list of rotors used in the machine
        for(index=0; index<rotorIds.size()-1; index++)
            res = res + rotorIds.get(index) + ',';
        res += rotorIds.get(index) + '>';

        // adding initial positions of rotors
        for(index=0; index<rotorPositions.size()-1; index++)
            res = res + rotorPositions.get(index) + ',';
        res += rotorPositions.get(index) + '>';

        res += res + '<' + reflectorID + '>';

        return res;

    }
}
