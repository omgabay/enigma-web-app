package controllers.setup;

import controllers.MainController;
import exceptions.EnigmaException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import machine.parts.Plugboard;
import model.MachineModel;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This controller controls the Setup Tab in the Enigma App
 * Where you configure and Set up your machine
 */
public class SetupController {


    @FXML HBox codeCalibrationPanel;
    @FXML ToggleGroup reflectorSelection;      // Reflector RadioButton Toggle group

    @FXML HBox idsCbox;
    @FXML HBox positionsCbox;



    // FXML Labels
@FXML Label rotorDetail;
@FXML Label reflectorDetail;
@FXML Label alphabetDetail;
@FXML Label dictionaryDetail;

//Current configuration and starting Labels
@FXML Label currentConfigurationLabel;
@FXML Label initialConfigurationLabel;


// Plugboard components
@FXML Text addPlugsText;

@FXML TextField plugsInputTF;



    private final SimpleStringProperty alphabetProperty;
    private final SimpleIntegerProperty numberOfReflectorsProperty;

    private final SimpleIntegerProperty totalNumberOfRotorsProperty;
    private final SimpleIntegerProperty numberOfMachineRotorsProperty;


    private List<ComboBox<Integer>> rotorNumbersCB;
    private List<ComboBox<Character>> rotorPositionsCB;

    private final HashMap<Character,Character> plugboard;


    private MainController mainController;
    private MachineModel model;
    boolean fileIsLoaded = false;



    // Empty constructor --> FXML load will call it
    public SetupController(){

        alphabetProperty = new SimpleStringProperty();
        numberOfReflectorsProperty = new SimpleIntegerProperty();

        totalNumberOfRotorsProperty = new SimpleIntegerProperty();
        numberOfMachineRotorsProperty = new SimpleIntegerProperty();

        plugboard = new HashMap<>();

    }

    @FXML
    public void initialize(){
        this.model = MachineModel.getInstance();
    }


    public void createMachineSetupView() {

        if(fileIsLoaded){
            clearSetupView();
        }
        codeCalibrationPanel.setDisable(false);

        int totalNumberOfRotors = this.totalNumberOfRotorsProperty.get();

        List<Integer> range = IntStream.rangeClosed(1,totalNumberOfRotors).boxed().collect(Collectors.toList());

        // Creating new lists for ComboBoxes
        rotorNumbersCB = new ArrayList<>();
        rotorPositionsCB = new ArrayList<>();

        // Creating ComboBoxes for Rotor Selection
        int rotorsCount = this.numberOfMachineRotorsProperty.get();
        for(int i=0; i<rotorsCount; i++){
            ComboBox<Integer> cb = new ComboBox<>(FXCollections.observableArrayList(range));
            cb.getSelectionModel().select(0);
            this.idsCbox.getChildren().add(cb);
            this.rotorNumbersCB.add(cb);
        }

        // Creating ComboBoxes for Rotor Positions Selection
        String abc = this.alphabetProperty.get();
        List<Character> letters = abc.chars().mapToObj(c -> (char) c).collect(Collectors.toList());

        for(int i=0; i<rotorsCount; i++){
            ComboBox<Character> cb = new ComboBox<>(FXCollections.observableArrayList(letters));
            //cb.setMaxWidth(20);
            cb.getSelectionModel().select(0);
            this.positionsCbox.getChildren().add(cb);
            this.rotorPositionsCB.add(cb);
        }

        int reflectorsCount = this.numberOfReflectorsProperty.get();
        for (Toggle toggle : this.reflectorSelection.getToggles()) {
            RadioButton radioButton = (RadioButton) toggle;
            if(reflectorsCount <= 0){
                radioButton.setVisible(false);
            }
            reflectorsCount--;
        }

        this.fileIsLoaded = true;
    }

    private void clearSetupView() {
        this.idsCbox.getChildren().clear();
        this.positionsCbox.getChildren().clear();
    }


    public void randomButtonPressed(){

        model.createRandomMachine();

        mainController.updateUIAfterSetup();

    }



    public void createButtonPressed(){
        List<Integer> rotorIds = new ArrayList<>();
        Set<Integer> validator = new HashSet<>();
        int id;

        HashMap<Character,Character> result = checkForValidPlugs();
        if(result == null){
            createAlert("Inserted invalid plugs ");
            return;
        }
        this.plugboard.clear();
        this.plugboard.putAll(result);


        try{
            for (ComboBox<Integer> comboBox : this.rotorNumbersCB) {
                id = comboBox.getSelectionModel().getSelectedItem();
                if (validator.contains(id)) {
                    throw new EnigmaException("Wrong User Input - Rotor ID Selection");
                }

                validator.add(id);
                rotorIds.add(0,comboBox.getSelectionModel().getSelectedItem());
            }
        } catch (EnigmaException e) {
            createAlert("You chose a rotor more than once.\n Rotor IDs have to be unique.");
            return;
        }

        // getting rotor positions from Cbox
        List<Character> rotorPositions = new ArrayList<>();
        for (ComboBox<Character> comboBox  :  this.rotorPositionsCB) {
            rotorPositions.add(0,comboBox.getSelectionModel().getSelectedItem());
        }

        // getting reflector ID from toggle group
        RadioButton selectedRadioButton = (RadioButton) reflectorSelection.getSelectedToggle();
        String reflector = selectedRadioButton.getText();
        System.out.println("User chose reflector" + reflector);

        Collections.reverse(rotorIds);
        Collections.reverse(rotorPositions);

        MachineModel.getInstance().setupMachine(rotorIds,rotorPositions,reflector, plugboard);
        mainController.updateUIAfterSetup();


    }

    public void bindWithModelProperties() {
        alphabetProperty.bind(model.getAlphabetProperty());
        numberOfReflectorsProperty.bind(model.totalNumberOfReflectorsProperty());

        // binding rotors details
        totalNumberOfRotorsProperty.bind(model.totalNumberOfRotorsProperty());
        numberOfMachineRotorsProperty.bind(model.numberOfMachineRotorsProperty());

        // Details pane
        rotorDetail.textProperty().bind(model.totalNumberOfRotorsProperty().asString());
        reflectorDetail.textProperty().bind(model.totalNumberOfReflectorsProperty().asString());
        alphabetDetail.textProperty().bind(model.getAlphabetProperty());


        // Current machine configuration view
        this.initialConfigurationLabel.textProperty().bind(model.initialConfigurationStringFormatProperty());
        this.currentConfigurationLabel.textProperty().bind(model.currentConfigurationStringFormatProperty());

        this.dictionaryDetail.textProperty().bind(model.dictionarySizeProperty().asString());


    }

    private HashMap<Character, Character> checkForValidPlugs() {
        String insertedPlugs = plugsInputTF.getText().toUpperCase();
        int numberOfPlugs = insertedPlugs.length();
        HashMap<Character,Character> plugMapping = new HashMap<>();

        Plugboard plugs = new Plugboard(model.getEngine().getMachineAlphabet());

        if (numberOfPlugs % 2 != 0) {
            return null;
        }
        for(int index = 0; index < numberOfPlugs - 1; index+=2){
            char firstPlug = insertedPlugs.charAt(index);
            char secondPlug = insertedPlugs.charAt(index + 1);
            try {
                plugMapping.put(firstPlug,secondPlug);
                if (!plugs.addToPlugboard(firstPlug, secondPlug)) {
                    return null;
                }
            }catch (EnigmaException e){
                return null;
            }
        }
        return plugMapping;
    }

    public void AddPlugsTFMouseClickedListener() {
        addPlugsText.setText("Add plugs by inserting pairs of letters according to the following list:\n" +
                MachineModel.getInstance().getAlphabetProperty().getValue().toString());

    }

    private void createAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR,message);
        alert.setHeaderText("Wrong Input :(");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        //stage.getIcons().add(new Image("/images/turing_icon.png"));
        alert.show();
    }


    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

}
