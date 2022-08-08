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
import machine.MachineInfo;
import machine.parts.Plugboard;
import machine.parts.Reflector;
import machine.parts.Rotor;
import exceptions.WrongFileFormatException;

public class Engine implements IEngine {
    private Enigma enigmaMachine;
    private boolean isLoaded;
    private List<Integer> rotorsInitialConfig;
    private List<Rotor> rotorList;
    private List<Reflector> reflectorList;

    // machine instance variables
    private Alphabet alphabet;
    private int rotorsCount;


    private HashMap<Integer, Integer> mapRotorIDToPosition;
    public Engine(){
        this.rotorList = new ArrayList<>();
        this.reflectorList = new ArrayList<>(5);
        this.enigmaMachine = null;
        this.isLoaded = false;
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
            throw new InvalidConfigurationException("You should have an even number of letters in your alphabet");
        }
        this.rotorsCount = m.getRotorsCount();
        this.rotorList = new ArrayList<>();
        for (CTERotor rotor: m.getCTERotors().getCTERotor()) {
            rotorList.add(new Rotor(rotor, this.alphabet));
        }
        if(rotorsCount < 2 || rotorsCount > 99 || rotorsCount > rotorList.size()){
            String msg = "Invalid Rotors Count - make sure you use at least 2 rotors and no more than the allowed amount";
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
        MachineSettings settings = new MachineSettings(rotorsCount, rotorList.size(), reflectorList.size(), this.alphabet);
        return new EngineResponse<>(settings, true);
    }

    @Override
    public EngineResponse<Integer> displayMachine() {
        return new EngineResponse<>(3, true);
    }

    @Override
    public void setupMachine(List<Integer> rotorIDs, List<Character> rotorPositions, RomanNumeral reflectorID, Map<Character, Character> plugs) {
        List<Rotor> machineRotors = new ArrayList<>();
        Iterator<Character> it = rotorPositions.listIterator();
        for (int id : rotorIDs) {
            Rotor r = this.rotorList.get(id-1);
            r.setPosition(alphabet.getOrder(it.next()));
            machineRotors.add(r);
        }
        Reflector reflector = getReflectorByID(reflectorID).orElseThrow(ReflectorNotFound::new);
        Plugboard pb = new Plugboard(plugs, alphabet);
        this.enigmaMachine = new EnigmaMachine(machineRotors,reflector, pb, alphabet, true);
    }

    @Override
    public EngineResponse<MachineSetup> setupMachineAtRandom() {
        Random r = new Random();
        Set<Integer> rotors = new HashSet<>();
        List<Rotor> machineRotors = new ArrayList<>();
        while(rotors.size() < this.rotorsCount) {
            int i = r.nextInt(rotorList.size());
            if (!rotors.contains(i)) {
                Rotor rotor = rotorList.get(i);
                rotor.setPosition(r.nextInt(alphabet.size()));
                machineRotors.add(rotor);
                rotors.add(i);
            }
        }

        Reflector machineReflector = reflectorList.get(r.nextInt(this.reflectorList.size()));
        Plugboard pb = new Plugboard(this.alphabet);
        for (char letter : alphabet.getABC()) {
            int probability = r.nextInt(100);
            if(probability >= 90){
                char letter2  = alphabet.getLetter(r.nextInt(alphabet.size()));
                if(letter != letter2){
                    pb.addToPlugboard(letter,letter2);
                }
            }
        }
        enigmaMachine = new EnigmaMachine(machineRotors, machineReflector, pb, alphabet, true);
        MachineSetup ms = new MachineSetup((MachineInfo)enigmaMachine);
        return new EngineResponse<>(ms,true);
    }

    @Override
    public String processText(String text) {
        return enigmaMachine.processText(text);
    }

    @Override
    public void resetMachine() {

    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
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
            r.validateRotor();
        }

        if(reflectorList.size() > 5 || reflectorList.isEmpty()){
            String msg = "The number of reflectors has to be no more than 5 and at least 1";
            throw new InvalidConfigurationException(msg);
        }


        EnumSet<RomanNumeral> romans = EnumSet.noneOf(RomanNumeral.class);
        for (Reflector ref : reflectorList){
            ref.validateReflector();
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
