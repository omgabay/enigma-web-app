package model;


import auxiliary.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import machine.Enigma;
import machine.IEngine;
import machine.parts.Plugboard;
import machine.parts.Reflector;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;


public class MachineModel {

    private static volatile MachineModel instance;

    private IEngine engine;
    private Enigma machine;


    // Machine related fields

    // The chosen rotors inside the machine
    private final ObservableList<Integer> machineRotors;

    private final SimpleIntegerProperty totalNumberOfRotors;
    private final SimpleIntegerProperty totalNumberOfReflectors;
    private final SimpleIntegerProperty numberOfMachineRotors;

    // The rotors current positions
    private final ObservableList<Character> positions;




    //Property for reflector
    private SimpleObjectProperty<Reflector> reflectorProperty;

    private SimpleObjectProperty<Plugboard> plugboardProperty;

    private final SimpleStringProperty alphabetProperty;

    private SimpleStringProperty initialConfigurationStringFormat;
    private final SimpleStringProperty currentConfigurationStringFormat;



    private final SimpleObjectProperty<Dictionary> dictionaryProperty;

    private final ObservableList<Message> messages;

    private final SimpleIntegerProperty dictionarySizeProperty;


    private MachineModel(){

        this.machineRotors = FXCollections.observableArrayList();
        this.positions = FXCollections.observableArrayList();

        this.totalNumberOfRotors = new SimpleIntegerProperty(0);
        this.numberOfMachineRotors = new SimpleIntegerProperty(0);
        this.totalNumberOfReflectors = new SimpleIntegerProperty(0);


        this.messages = FXCollections.observableArrayList();
        this.alphabetProperty = new SimpleStringProperty();

        this.initialConfigurationStringFormat = new SimpleStringProperty("Machine was not created");
        this.currentConfigurationStringFormat = new SimpleStringProperty("Machine was not created");

        // Dictionary Properties
        this.dictionaryProperty = new SimpleObjectProperty<>(null);
        this.dictionarySizeProperty = new SimpleIntegerProperty(0);

    }


    public static MachineModel getInstance(){
        MachineModel result = instance;
        if(result == null){
            synchronized (MachineModel.class){
                result = instance;
                if(result == null) {
                    instance = result = new MachineModel();
                }
            }
        }
        return result;
    }


    public void updateAfterLoad(){

        // Creating property for alphabet object
        alphabetProperty.set(engine.getMachineAlphabet().toString());

        // Creating property for dictionary
        Dictionary dictionary = engine.getDictionary();
        dictionaryProperty.setValue(dictionary);
        dictionarySizeProperty.set(dictionary.getWordsList().size());







        this.totalNumberOfRotors.set(engine.getTotalAvailableRotors());
        this.numberOfMachineRotors.set(engine.getNumberOfMachineRotors());

        this.totalNumberOfReflectors.set(engine.getTotalAvailableReflectors());
    }




    public void setupMachine(List<Integer> rotors, List<Character> positions, String reflector, Map<Character, Character> plugs){
        this.machine = engine.setupMachine(rotors,positions,reflector,plugs);
        updateModelAfterSetup();
    }

    public void createRandomMachine() {
        this.machine = engine.setupMachineAtRandom();

        updateModelAfterSetup();
    }

    public void updateModelAfterSetup(){
        // fetching information about the machine from the engine
        MachineInfo info = (MachineInfo) instance.engine.getMachineInfo().getData();

        // getting rotor information
        machineRotors.clear();
        machineRotors.addAll(info.getRotorIDs());

        positions.clear();
        positions.addAll(info.getRotorPositions());

        // getting reflector object
        reflectorProperty = new SimpleObjectProperty<>(info.getReflector());

        // getting plugboard object
        plugboardProperty = new SimpleObjectProperty<>(info.getPlugboard());

        // Updating initial configuration
        this.initialConfigurationStringFormat.set(info.toString());
        this.currentConfigurationStringFormat.set(info.getCurrentConfiguration());

    }






    /**
     * @param textToProcess text to send for Enigma for processing Encryption / Decryption
     * @param addToStats   set to True if you want to log the process operation
     * @param resetMachine set to True if you want the machine to reset to initial positions after operation
     */
    public String processText(String textToProcess, boolean addToStats , boolean resetMachine) {
        if(!checkForValidString(textToProcess)){
            return "";
        }
        EngineResponse<?> er = engine.processText(textToProcess);
        Message processed = (Message) er.getData();
        if(resetMachine){
            engine.resetMachine();
        }
        if(addToStats){
            messages.add(processed);
        }

        // Rotors Positions  were changed --> updating the observable list of rotor positions
        instance.positions.clear();
        instance.positions.addAll(machine.getRotorPositions());

        // Updating initial configuration
        this.initialConfigurationStringFormat.set(machine.toString());
        this.currentConfigurationStringFormat.set(machine.getCurrentConfiguration());

        return processed.getProcessed();
    }


    public void load(File xml) throws JAXBException, FileNotFoundException {
        this.engine.loadMachineFromXml(xml);
    }

    private boolean checkForValidString(String textToProcess) {
        boolean returnValue = true;
        for(char ch : textToProcess.toCharArray()){
            if(engine.getMachineAlphabet().isLetter(ch) == false){
                returnValue = false;
                break;
            }
        }
        return returnValue;
    }



    public SimpleStringProperty getAlphabetProperty(){
        return this.alphabetProperty;
    }


    public int getTotalNumberOfRotors() {
        return totalNumberOfRotors.get();
    }

    public SimpleIntegerProperty totalNumberOfRotorsProperty() {
        return totalNumberOfRotors;
    }

    public int getTotalNumberOfReflectors() {
        return totalNumberOfReflectors.get();
    }

    public SimpleIntegerProperty totalNumberOfReflectorsProperty() {
        return totalNumberOfReflectors;
    }

    public Dictionary getDictionary() {
        return dictionaryProperty.get();
    }

    public SimpleObjectProperty<Dictionary> dictionaryProperty() {
        return dictionaryProperty;
    }

    public String getInitialConfigurationStringFormat() {
        return initialConfigurationStringFormat.get();
    }

    public SimpleStringProperty initialConfigurationStringFormatProperty() {
        return initialConfigurationStringFormat;
    }

    public SimpleObjectProperty<Reflector> reflectorProperty() {
        return reflectorProperty;
    }

    public SimpleStringProperty currentConfigurationStringFormatProperty() {
        return currentConfigurationStringFormat;
    }

    public int getNumberOfMachineRotors() {
        return numberOfMachineRotors.get();
    }

    public SimpleIntegerProperty numberOfMachineRotorsProperty() {
        return numberOfMachineRotors;
    }

    public ObservableList<Message> getMessages() {
        return messages;
    }

    public void setEngine(IEngine engine){
        this.engine = engine;
    }


    public void resetMachineEvent() {
        if(this.machine == null){
            return;
        }
        this.initialConfigurationStringFormat.set(machine.toString());
        this.currentConfigurationStringFormat.set(machine.getCurrentConfiguration());
        this.engine.resetMachine();
    }

    public IEngine getEngine() {
        return engine;
    }

    public ObservableList<Integer> getMachineRotors() {
        return this.machineRotors;
    }

    public ObservableList<Character> getRotorPositions() {
        return this.positions;
    }



    public SimpleIntegerProperty dictionarySizeProperty() {
        return dictionarySizeProperty;
    }

    public long getAllRotorCombinations() {
        int base = this.alphabetProperty.get().length();
        int rotors = this.machineRotors.size();
        return (long) Math.pow((double) base, (double) rotors);
    }


}
