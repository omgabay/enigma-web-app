import auxiliary.EngineResponse;
import auxiliary.RomanNumeral;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IEngine extends Serializable {

    public EngineResponse<?> loadMachineFromXml(File xml) throws FileNotFoundException, JAXBException;

    public EngineResponse<?> displayMachine();

    public EngineResponse<?> showHistory();




    public void setupMachine(List<Integer> rotorIDs, List<Character> rotorPositions, RomanNumeral reflectorID, Map<Character, Character> plugs);

    public EngineResponse<?> setupMachineAtRandom();



    /**
     * @param text input text for the machine to be encrypted or decrypted
     * @return processed text
     */
    public EngineResponse<?> processText(String text);


    /**
     * Resets enigma machine to initial setup - all rotors are returned to initial positions.
     */
    public void resetMachine();


    public int getNumOfReflectors();

    public int getNumOfRotors();
    
    public EngineResponse<?> getEnigmaSettings();


    public boolean isLoaded();
    public boolean isMachinePresent();

}
