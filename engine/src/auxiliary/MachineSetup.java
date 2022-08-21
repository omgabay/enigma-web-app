package auxiliary;
import machine.parts.Plugboard;

import java.util.List;

public class MachineSetup {
    private List<Integer> rotorIDs;
    private List<Character> rotorPositions;
    private RomanNumeral reflectorID;
    private Plugboard pb;


    public MachineSetup(MachineInfo info){
        rotorIDs = info.getRotorIDs();
        rotorPositions = info.getRotorPositions();
        reflectorID = info.getReflector().getID();
        pb = info.getPlugboard();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int rotor : rotorIDs){
            if(sb.length() == 0){
                sb.append('<');
            }else{
                sb.append(',');
            }
            sb.append(rotor);
        }
        sb.append("><");

        for(int i=0; i<rotorPositions.size(); i++){
            if(i != 0){
                sb.append(',');
            }
            sb.append(rotorPositions.get(i));
        }
        sb.append("><").append(reflectorID.toString()).append(">");
        sb.append(pb.toString());


    return sb.toString();
    }
}
