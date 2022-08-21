import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import auxiliary.*;
import exceptions.EnigmaException;
import exceptions.InvalidConfigurationException;
import exceptions.ReflectorNotFound;
import generated.CTEEnigma;
import generated.CTEMachine;
import generated.CTEReflector;
import generated.CTERotor;
import java.io.File;
import java.util.*;
import machine.Enigma;
import machine.EnigmaMachine;
import auxiliary.MachineInfo;
import machine.parts.Plugboard;
import machine.parts.Reflector;
import machine.parts.Rotor;
import exceptions.WrongFileFormatException;

public class Engine implements IEngine {
    private Enigma enigmaMachine;
    private boolean isLoaded;
    private boolean isMachinePresent;

    private List<Rotor> rotorList;
    private List<Reflector> reflectorList;

    private final List<History> histories;
    private History currentHistory;

    // machine instance variables
    private Alphabet alphabet;
    private int rotorsCount;


    private HashMap<Integer, Integer> mapRotorIDToPosition;
    public Engine(){
        this.rotorList = new ArrayList<>();
        this.reflectorList = new ArrayList<>(5);
        this.enigmaMachine = null;
        this.histories = new ArrayList<>();
        this.currentHistory = null;
        this.isLoaded = false;
        this.isMachinePresent = false;
    }

    @Override
    public EngineResponse<MachineSettings> loadMachineFromXml(File xml) throws RuntimeException, JAXBException {
        String fileName =  xml.getName();
        int index = fileName.lastIndexOf('.');
        if(index < 0){
            throw new WrongFileFormatException(fileName);
        }else{
            String extension = fileName.substring(index);
            if(!extension.equals(".xml")){
                throw new WrongFileFormatException(fileName);
            }
        }


        JAXBContext jaxbContext = JAXBContext.newInstance(CTEEnigma.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        CTEEnigma enigma =  (CTEEnigma) jaxbUnmarshaller.unmarshal(xml);
        CTEMachine m = enigma.getCTEMachine();

        this.alphabet = new Alphabet(m.getABC().trim());

        if(alphabet.size() % 2 != 0)
        {
            System.out.println("alphabet size:" + alphabet.size());
            System.out.println(alphabet.getABC());
            throw new InvalidConfigurationException("\nInvalid configuration file, the machine alphabet has to have an even number of letters\n");
        }
        this.rotorsCount = m.getRotorsCount();
        this.rotorList = new ArrayList<>();
        for (CTERotor rotor: m.getCTERotors().getCTERotor()) {
            rotorList.add(new Rotor(rotor, this.alphabet));
        }
        if(rotorsCount < 2 || rotorsCount > 99 || rotorsCount > rotorList.size()){
            String msg = "Invalid configuration, rotors-count has to be at least 2 and no more than the available rotors.";
            throw new InvalidConfigurationException(msg);
        }
        // Sorting rotorList by ID
        rotorList.sort((r1,r2) -> r1.getID() - r2.getID());

        this.reflectorList = new ArrayList<>(5);
        for(CTEReflector reflect : m.getCTEReflectors().getCTEReflector()){
            reflectorList.add(new Reflector(reflect));
        }

        checkForErrors();
        isLoaded = true;
        isMachinePresent = false;
        MachineSettings settings = new MachineSettings(rotorsCount, rotorList.size(), reflectorList.size(), this.alphabet);
        return new EngineResponse<>(settings, true);
    }

    @Override
    public EngineResponse<MachineInfo> displayMachine() {

        return new EngineResponse<>(enigmaMachine, true);
    }



    @Override
    public void setupMachine(List<Integer> rotorIDs, List<Character> rotorPositions, RomanNumeral reflectorID, Map<Character, Character> plugs) {
        List<Rotor> machineRotors = new ArrayList<>();
        Iterator<Character> it = rotorPositions.listIterator();
        for (int id : rotorIDs) {
            Rotor r = this.rotorList.get(id-1);
            r.setInitialPosition(it.next());
            machineRotors.add(r);
        }
        Reflector reflector = getReflectorByID(reflectorID).orElseThrow(ReflectorNotFound::new);
        Plugboard pb = new Plugboard(plugs, alphabet);
        this.enigmaMachine = new EnigmaMachine(machineRotors,reflector, pb, alphabet);
        if(currentHistory != null){
            this.histories.add(currentHistory);
        }
        this.currentHistory = new History(enigmaMachine.toString());
        this.isMachinePresent = true;
    }

    @Override
    public EngineResponse<MachineInfo> setupMachineAtRandom() {
        Random r = new Random();
        Set<Integer> rotors = new HashSet<>();
        List<Rotor> machineRotors = new ArrayList<>();
        while(rotors.size() < this.rotorsCount) {
            int i = r.nextInt(rotorList.size());
            if (!rotors.contains(i)) {
                Rotor rotor = rotorList.get(i);
                machineRotors.add(rotor);
                rotors.add(i);
            }
        }

        // Set Rotors at Random Position
        for(Rotor rotor : machineRotors){
            int pos = r.nextInt(this.alphabet.size());
            rotor.setInitialPosition(alphabet.getLetter(pos));
        }

        // Pick Reflector at Random
        Reflector machineReflector = reflectorList.get(r.nextInt(this.reflectorList.size()));

        // Random Generate Plugboard
        Plugboard pb = new Plugboard(this.alphabet);
        for (char letter : alphabet.getABC()) {
            int probability = r.nextInt(100);
            if(probability >= 90){
                char letter2  = alphabet.getLetter(r.nextInt(alphabet.size()));
                if(letter != letter2){
                    // addToPlugboard will not add the pair in case one of the letters is in use
                    pb.addToPlugboard(letter,letter2);
                }
            }
        }

        enigmaMachine = new EnigmaMachine(machineRotors, machineReflector, pb, alphabet);
        if(currentHistory != null){
            this.histories.add(currentHistory);
        }
        this.currentHistory = new History(enigmaMachine.toString());
        this.isMachinePresent = true;
        return new EngineResponse<>(enigmaMachine,true);
    }

    @Override
    public EngineResponse<Message> processText(String text) {
        Message m = enigmaMachine.processText(text);
        currentHistory.add(m);
        return new EngineResponse<>(m,true);
    }

    @Override
    public void resetMachine() {
        this.enigmaMachine.resetMachine();
    }

    @Override
    public int getNumOfReflectors() {
        return this.reflectorList.size();
    }

    @Override
    public int getNumOfRotors() {
        return this.rotorList.size();
    }

    @Override
    public EngineResponse<MachineSettings> getEnigmaSettings() {
        if(!this.isLoaded){
            return new EngineResponse<>(null,false);
        }

        MachineSettings ms = new MachineSettings(rotorsCount, rotorList.size(), reflectorList.size(), this.alphabet);
        return new EngineResponse<>(ms, true);
    }

    @Override
    public EngineResponse<?> showHistory() {
        if(currentHistory == null){
            return new EngineResponse<>(null,false);
        }
        List<History> res = new ArrayList<>(histories);
        res.add(currentHistory);
        return new EngineResponse<>(res,true);
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public boolean isMachinePresent() {
        return isMachinePresent;
    }

    public void checkForErrors() throws EnigmaException{
        int listSize = rotorList.size();
        if(rotorList.get(0).getID() != 1 || rotorList.get(listSize-1).getID() != listSize){
            String seq = "(1," + listSize +")";
            throw new InvalidConfigurationException("Invalid/Missing rotor ID in sequence " + seq);
        }
        if(rotorsCount < 2){
            throw new InvalidConfigurationException("Number of rotors used should be at least 2.");
        } else if (rotorsCount > rotorList.size()) {
            throw new InvalidConfigurationException("Number of rotors used should be less than or equal to the total number of rotors.");
        }

        for (Rotor r : rotorList) {
            r.validateRotor(this.alphabet);
        }

        if(reflectorList.size() > 5 || reflectorList.isEmpty()){
            String msg = "The number of reflectors has to be no more than 5 and at least 1";
            throw new InvalidConfigurationException(msg);
        }


        EnumSet<RomanNumeral> romans = EnumSet.noneOf(RomanNumeral.class);
        for (Reflector ref : reflectorList){
            ref.validateReflector(alphabet);
            RomanNumeral rn = ref.getID();
            if(romans.contains(rn)){
                String msg = "Reflector " +rn.name() + " is duplicated!";
                throw new InvalidConfigurationException(msg);
            }
            romans.add(ref.getID());
        }

        // Check if roman numbers are in sequence
        int i = 1;
        for(RomanNumeral rn : romans){
            if(rn.getValue() != i){
                String msg = "Reflector " + RomanNumeral.getRomanFromInt(i) + " is missing!";
                throw new InvalidConfigurationException(msg);
            }
            i++;
        }

    }



    private Optional<Reflector> getReflectorByID(RomanNumeral roman){
        Optional<Reflector> result = Optional.empty();
        for(Reflector ref : this.reflectorList){
            if (roman == ref.getID())
                return Optional.ofNullable(ref);
        }
        return result;
    }


}
