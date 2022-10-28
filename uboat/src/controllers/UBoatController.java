package controllers;
import auxiliary.Dictionary;
import auxiliary.Message;
import auxiliary.Battlefield;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import machine.Engine;
import machine.Enigma;
import machine.IEngine;
import main.UBoatClient;
import okhttp3.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import requests.UBoatClientRefresher;
import users.AgentEntry;
import users.AllyTeam;
import users.UBoat;

import static utils.Configuration.*;
import static utils.Constants.REFRESH_RATE;
import static utils.http.HttpClientUtil.HTTP_CLIENT;

public class UBoatController {

    private Stage stage;

    @FXML TextField fileLoadLabel;

    @FXML Label nameLabel;


    @FXML Label rotorCountLabel;

    @FXML Label machineRotorCountLabel;

    @FXML Label reflectorCountLabel;

    @FXML Label alphabetLabel;

    @FXML Label battlefieldLabel;

    @FXML Label difficultyLabel;

    @FXML ToggleGroup reflectorSelection;


    // ComboBoxes for Code setup

    @FXML HBox rotorPositionsCbox;

    @FXML HBox rotorIdsCbox;


    // Current Machine Setup Label

    @FXML Label currentSetupLabel;

    @FXML TextField uboatMessage;

    @FXML TextField secretMessage;

    @FXML ComboBox<String> dictionaryCbox;

    @FXML TableView<AllyTeam> teamsTableView;

    @FXML TableColumn<AllyTeam, String> teamNameCol;
    @FXML TableColumn<AllyTeam, Integer> agentCountCol;
    @FXML TableColumn<AllyTeam, Integer> missionSizeCol;



    private final StringProperty uboatNameProperty;
    private final StringProperty loadStatusMessageProperty;

    private final StringProperty currentMachineSetupProperty = new SimpleStringProperty("");

    private final IEngine engine = new Engine();

    private Enigma enigmaMachine;
    private UBoat uboat;



    public UBoatController(UBoat uboat){
        this.uboat = uboat;
        uboatNameProperty = new SimpleStringProperty(uboat.getName() + "'s UBoat");
        loadStatusMessageProperty = new SimpleStringProperty("");


    }

    @FXML
    public void initialize(){
        fileLoadLabel.textProperty().bind(loadStatusMessageProperty);
        nameLabel.textProperty().bind(uboatNameProperty);
        currentSetupLabel.textProperty().bind(currentMachineSetupProperty);

        // Teams Table
        teamNameCol.setCellValueFactory(
                cellData -> cellData.getValue().NameProperty()
        );;
        agentCountCol.setCellValueFactory(
                cellData -> cellData.getValue().getAgentCount()
        );
        missionSizeCol.setCellValueFactory(
                cellData -> cellData.getValue().missionSizeProperty()
        );

    }


    public void loadFileClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Enigma XML File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Enigma XML", "*.xml"));
        File file = fileChooser.showOpenDialog(stage);

        if(file == null){
            return;
        }


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
                        engine.loadFromJson(json);


                        Platform.runLater(() -> {
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

        // Update dictionary
        Dictionary dictionary = engine.getDictionary();
        ObservableList<String> words = FXCollections.observableArrayList(dictionary.getWordsList());
        this.dictionaryCbox.setItems(words);




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

    public void createContestClicked(ActionEvent actionEvent){}

    public void setMachineCodeClicked(ActionEvent event){
        // Collecting user input for machine setup
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
        this.enigmaMachine = engine.getMachine();

        // Updating current machine configuration label after setup
        String currentMachineConfiguration = engine.getMachine().getCurrentConfiguration();
        this.currentMachineSetupProperty.set(currentMachineConfiguration);
    }

    public void createRandomMachineCode(ActionEvent event){
        engine.setupMachineAtRandom();
        String currentMachineConfiguration = engine.getMachine().getCurrentConfiguration();
        this.currentMachineSetupProperty.set(currentMachineConfiguration);
        System.out.println(currentMachineConfiguration);
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


     public void addWordToMessage(ActionEvent event){
        String word = (String) dictionaryCbox.getSelectionModel().getSelectedItem();
        // Getting message from uboat text field
        String message = this.uboatMessage.getText();
        if(!message.isEmpty()) {
            message = message + " " + word;
        }else{
            message = word;
        }

        this.uboatMessage.setText(message);
        this.updateSecretMessage(message);



    }

    private void updateSecretMessage(String original) {
        if(enigmaMachine == null){
            return;
        }
        this.enigmaMachine.resetMachine();
        Message m = this.enigmaMachine.processText(original);
        String encrypted = m.getProcessed();
        this.secretMessage.setText(encrypted);

    }


    private void updateTeamsTable(List<AllyTeam> teamsList){
        teamsTableView.getItems().clear();
        teamsTableView.getItems().addAll(teamsList);
    }


    public void startUBoatRefresher(){
        UBoatClientRefresher clientRefresher = new UBoatClientRefresher(this::updateTeamsTable);
        Timer timer = new Timer();
        timer.schedule(clientRefresher,2*REFRESH_RATE,REFRESH_RATE);
    }

}
