package logic.uboat;

import jaxb.generated.CTEEnigma;

import java.util.HashMap;
import java.util.Map;

public class UBoatManager {

    private final Map<String, CTEEnigma> mapUboatToEnigmaMachine;


    public UBoatManager(){
        mapUboatToEnigmaMachine = new HashMap<>();
    }


    public CTEEnigma getMachine(String uboatName){
        return mapUboatToEnigmaMachine.getOrDefault(uboatName, null);
    }


    public void addMachine(String uboatName, CTEEnigma machine){
        mapUboatToEnigmaMachine.put(uboatName, machine);
    }




}
