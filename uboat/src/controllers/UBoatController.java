package controllers;
import ex3.Battlefield;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import machine.Engine;
import machine.IEngine;
import okhttp3.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;

import static utils.Configuration.*;
import static utils.http.HttpClientUtil.HTTP_CLIENT;

public class UBoatController {

    private Stage stage;

    @FXML TextField fileLoadLabel;

    @FXML Label nameLabel;


    @FXML
    private Label rotorCountLabel;

    @FXML
    private Label machineRotorCountLabel;

    @FXML
    private Label reflectorCountLabel;

    @FXML
    private Label alphabetLabel;

    @FXML
    private Label battlefieldLabel;

    @FXML
    private Label difficultyLabel;



    @FXML
    private ToggleGroup reflectorSelection;


    // ComboBoxes for Code setup

    @FXML
    private HBox rotorPositionsCbox;

    @FXML
    private HBox rotorIdsCbox;


    // Current Machine Setup Label

    @FXML
    private Label currentSetupLabel;




    @FXML
    private TextField uboatMessage;

    @FXML
    private TextField secretMessage;


    private final StringProperty uboatNameProperty;
    private final StringProperty loadStatusMessageProperty;

    private final IEngine engine = new Engine();



    public UBoatController(String uboatName){

        uboatNameProperty = new SimpleStringProperty(uboatName + "'s UBoat");
        loadStatusMessageProperty = new SimpleStringProperty("");

    }

    @FXML
    public void initialize(){
        fileLoadLabel.textProperty().bind(loadStatusMessageProperty);
        nameLabel.textProperty().bind(uboatNameProperty);
    }


    public void loadFileClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Enigma XML File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Enigma XML", "*.xml"));
        File file = fileChooser.showOpenDialog(stage);


        RequestBody body =
                new MultipartBody.Builder()
                        .addFormDataPart("enigma xml received named", file.getName(), RequestBody.create(file, MediaType.parse("text/plain")))
                        .build();

        Request request = new Request.Builder()
                .url(UPLOAD_FILE_URL)
                .post(body)
                .build();

        Call call = HTTP_CLIENT.newCall(request);

        call.enqueue(new Callback() {
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code() != 200){
                    String responseBody = response.body().string();
                    Platform.runLater(()->
                            loadStatusMessageProperty.set("Something went wrong: " + responseBody)
                    );
                }
                if(response.body() != null){
                    try {
                        String json = response.body().string();
                        System.out.println("uboat client received:" + json);
                        Platform.runLater(() -> {
                            engine.loadFromJson(json);
                            loadStatusMessageProperty.set(file.getName());
                            updateUI();



                        });

                    }catch(NullPointerException e){
                        e.printStackTrace();
                    }


                }

            }

            public void onFailure(Call call, IOException e) {

            }
        });
        Response response;
        try {
            response = call.execute();
        } catch (IOException e) {
            System.out.println("Error sending file to server");

            throw new RuntimeException(e);
        }


    }

    private void updateUI() {
        int totalNumOfRotors = engine.getTotalAvailableRotors();
        int machineRotorsSize = engine.getNumberOfMachineRotors();
        int reflectorCount = engine.getNumOfReflectors();
        String alphabet = engine.getMachineAlphabet().toString();

        // Update Machine Info
        rotorCountLabel.setText(String.valueOf(totalNumOfRotors));
        machineRotorCountLabel.setText(String.valueOf(machineRotorsSize));
        reflectorCountLabel.setText(String.valueOf(reflectorCount));
        alphabetLabel.setText(alphabet);


        // Update Battle Info
        Battlefield battle = engine.getBattlefield();
        battlefieldLabel.setText(battle.getBattleName());
        difficultyLabel.setText(battle.getBattleDifficulty());

        // Clear Cboxes
        this.rotorIdsCbox.getChildren().clear();
        this.rotorPositionsCbox.getChildren().clear();


        // Create Cboxes
        createRotorIDCbox(machineRotorsSize,totalNumOfRotors);
        createRotorPositionCbox(machineRotorsSize,alphabet);

        // Update Reflector Options
        updateReflectorOptions(reflectorCount);




    }

    private void createRotorPositionCbox(int machineRotorsSize, String alphabet) {
        List<Character> letters = alphabet.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        this.rotorPositionsCbox.setSpacing(10.0);

        for(int i=0; i<machineRotorsSize; i++){
            ComboBox<Character> cb = new ComboBox<>(FXCollections.observableArrayList(letters));
            //cb.setMaxWidth(20);
            cb.getSelectionModel().select(0);
            this.rotorPositionsCbox.getChildren().add(cb);
        }
    }

    /**
     * @param rotorsCount the number of rotors inside the machine
     * @param totalNumberOfRotors  the total number of rotors at our disposal      *
     * This function will create the comboboxes for choosing the rotors to use inside the machine
     * The function will be called after each time we load enigma xml file
     */
    private void createRotorIDCbox(int rotorsCount, int totalNumberOfRotors) {
        List<Integer> range = IntStream.rangeClosed(1,totalNumberOfRotors).boxed().collect(Collectors.toList());
        this.rotorIdsCbox.setSpacing(10.0);
        for(int i=0; i<rotorsCount; i++){
            ComboBox<Integer> cb = new ComboBox<>(FXCollections.observableArrayList(range));
            cb.getSelectionModel().select(0);
            this.rotorIdsCbox.getChildren().add(cb);
        }
    }

    private void updateReflectorOptions(int reflectorCount){
        int count = reflectorCount;
        for (Toggle toggle : this.reflectorSelection.getToggles()) {
            RadioButton radioButton = (RadioButton) toggle;
            if(count <= 0){
                radioButton.setVisible(false);
            }
            count--;
        }
    }

    public void logoutClicked(ActionEvent actionEvent) {}

    public void addWordToMessage(ActionEvent actionEvent){}
    public void createContestClicked(ActionEvent actionEvent){}

    public void setMachineCodeClicked(ActionEvent event){
        List<Integer> rotorIDs = fetchRotorIDSelection();
        List<Character> rotorPositions = fetchRotorPositionSelection();
        String reflectorChoice = getSelectedReflector();
        if(rotorIDs == null || rotorPositions == null){
            System.out.println("error");
            return;
        }
        System.out.println(Arrays.toString(rotorIDs.toArray()));
        System.out.println(Arrays.toString(rotorPositions.toArray()));
        System.out.println("Reflector " + reflectorChoice);

        engine.setupMachine(rotorIDs,rotorPositions, reflectorChoice, null);


        //engine.setupMachine()
    }

    private String getSelectedReflector() {
        RadioButton selected = (RadioButton) this.reflectorSelection.getSelectedToggle();
        return selected.getText();
    }

    private List<Integer> fetchRotorIDSelection() {
        List<Integer> ids = new ArrayList<>();
        for(Node node : this.rotorIdsCbox.getChildren()){
            if(node instanceof ComboBox){
                ComboBox<?> comboBox = (ComboBox<?>) node;
                Object selected = comboBox.getSelectionModel().getSelectedItem();
                if (selected instanceof Integer){
                    ids.add((Integer) selected);
                }else{
                    return null;
                }
            }else{
                return null;
            }
        }
        return ids;
    }


    private List<Character>  fetchRotorPositionSelection(){
        List<Character> rotorPositions = new ArrayList<>();
        for(Node node : this.rotorPositionsCbox.getChildren()){
            if(!(node instanceof ComboBox)){
                return null;
            }
            ComboBox<?> comboBox = (ComboBox<?>) node;
            Object selected = comboBox.getSelectionModel().getSelectedItem();

            if(!(selected instanceof Character)){
                return null;
            }
            rotorPositions.add((Character) selected);

        }
        return rotorPositions;
    }


    public void setStage(Stage stage){
        this.stage = stage;
    }

}
