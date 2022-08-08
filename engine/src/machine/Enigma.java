package machine;

import java.util.List;

public interface Enigma extends MachineInfo {


    /**
     * @param text  input text the operator will give to the machine.Enigma machine
     * @return outputs the encrypted string processed by the machine.Enigma
     */
    public String processText(String text);

    /**
     * Reset machine to initial configurations - all rotors go to initial positions
     */
    public void resetMachine();

    /**
     * Prints Machine configuration, this includes the following:
     *
     */
    public void printMachine();
    public void printHistory();




}
