package machine;

import auxiliary.RomanNumeral;
import machine.parts.Plugboard;
import machine.parts.Reflector;

import java.util.List;

/**
 * Interface for making queries and getting information about your enigma machine
 */
public interface MachineInfo {
    public List<Integer> getRotorIDs();
    public List<Character> getRotorPositions();

    public Reflector getReflector();

    public Plugboard getPlugboard();
}
