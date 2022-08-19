package machine.parts;
import auxiliary.RomanNumeral;
import exceptions.InvalidConfigurationException;
import generated.CTEReflect;
import generated.CTEReflector;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Reflector implements Serializable {
    private HashMap<Integer, Integer> reflectorMapping;

    private RomanNumeral id;
    private String idName;




    public Reflector(CTEReflector rafi) {
        this.idName = rafi.getId().toUpperCase();
        this.id = RomanNumeral.getRoman(idName);

        this.reflectorMapping = new HashMap<>();
        for (CTEReflect reflect : rafi.getCTEReflect()) {
            reflectorMapping.put(reflect.getInput()-1,reflect.getOutput()-1);
            reflectorMapping.put(reflect.getOutput()-1, reflect.getInput()-1);
        }
    }

    public Reflector(Reflector reflector) {
        this.reflectorMapping = new HashMap<>();
        this.reflectorMapping.putAll(reflector.reflectorMapping);
        this.id = reflector.id;
        this.idName = reflector.idName;
    }


    public int getReflection(int input){
        return this.reflectorMapping.get(input);
    }

    public void validateReflector() throws InvalidConfigurationException{
        if(id == RomanNumeral.Undefined){
            String msg = "Reflector with id " + idName + " is not a Roman number between I to V";
            throw new InvalidConfigurationException(msg);
        }


        // check validity of reflector mappings
        for (Map.Entry<Integer, Integer> pair:reflectorMapping.entrySet()) {
            if (Objects.equals(pair.getKey(), pair.getValue())){
                String msg = "Reflector " + this.idName + " has mapping from input to itself";
                throw new InvalidConfigurationException(msg);
            }

        }
    }

    public String toString(){
        return '<' + this.id.name() + '>';
    }

    public RomanNumeral getID(){ return this.id; }



}
