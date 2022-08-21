package machine;
import auxiliary.MachineInfo;
import auxiliary.Message;
import java.io.Serializable;

public interface Enigma extends MachineInfo, Serializable {


    /**
     * @param text  input text the operator will give to the machine.Enigma machine
     * @return outputs the encrypted string processed by the machine.Enigma
     */
    public Message processText(String text);

    /**
     * Reset machine to initial configurations - all rotors go to initial positions
     */
    public void resetMachine();








}
