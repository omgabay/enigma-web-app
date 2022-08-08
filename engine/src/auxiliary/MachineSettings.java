package auxiliary;

public class MachineSettings{
    private int numOfRotors;
    private int rotorsCount;
    private int numOfReflectors;
    private final Alphabet abc;


    public MachineSettings(int rotorsCount, int nRotors, int nReflectors, Alphabet alphabet){
        this.rotorsCount = rotorsCount;
        numOfRotors = nRotors;
        numOfReflectors = nReflectors;
        abc = alphabet;
    }
    public int getNumOfRotors(){
        return numOfRotors;
    }

    public int getRotorsCount() {
        return rotorsCount;
    }

    public int getAlphabetSize(){
        return abc.size();
    }

    public Alphabet getAlphabet() {return abc;}

    public RomanNumeral getMaximalReflector(){return RomanNumeral.getRomanFromInt(numOfReflectors);}
}
