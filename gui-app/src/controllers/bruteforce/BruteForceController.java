package controllers.bruteforce;
import auxiliary.Dictionary;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.MachineModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class BruteForceController {

    @FXML
    public BorderPane rootPaneBF;
    @FXML
    public ComboBox<String> difficultyCbox;
    @FXML
    public Button addButton;
    @FXML
    public ComboBox<String> dictionaryCbox;
    @FXML
    public TextField originalMessage;
    @FXML
    public TextField secretMessage;
    @FXML
    public Button startBFButton;


    // Sliders
    @FXML
    public Slider agentCountSlider;
    @FXML
    public TextField taskSizeTF;


    @FXML
    Button autoComplete1;
    @FXML
    Button autoComplete2;
    @FXML
    Button autoComplete3;
    @FXML
    Button autoComplete4;
    @FXML
    Button autoComplete5;
    @FXML
    TextField text;

    SimpleObjectProperty<Dictionary> dictionary;


    MachineModel model;
    List<Button> buttons;


    private enum DIFFICULTY_LVL {EASY, NORMAL, HARD, CRAZY}

    ;


    public BruteForceController() {
        buttons = new ArrayList<>();
        dictionary = new SimpleObjectProperty<>(null);
    }

    @FXML
    public void initialize() {
        this.model = MachineModel.getInstance();
        model.dictionaryProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            this.dictionaryCbox.setItems(FXCollections.observableArrayList(newValue.getWordsList()));
        }));



        if (model.dictionaryProperty().get() != null) {
            List<String> words = model.dictionaryProperty().get().getWordsList();
            this.dictionaryCbox.setItems(FXCollections.observableArrayList(words));
        }

        dictionary.bind(model.dictionaryProperty());



        ObservableList<String> difficulty = FXCollections.observableArrayList();
        for (DIFFICULTY_LVL level : DIFFICULTY_LVL.values()) {
            difficulty.add(level.name());
        }
        difficultyCbox.setItems(difficulty);
        difficultyCbox.getSelectionModel().select(0);

        buttons.add(autoComplete1);
        buttons.add(autoComplete2);
        buttons.add(autoComplete3);
        buttons.add(autoComplete4);
        buttons.add(autoComplete5);
        // action event


        // Adding listener to the textfield textProperty
        this.text.textProperty().addListener((observable, oldValue, newValue) -> {
            List<String> suggestions = dictionary.getValue().getWordSuggestions(newValue);
            //System.out.println(suggestions);
            Iterator<String> it = suggestions.iterator();
            for (Button button : buttons) {
                if (it.hasNext()) {
                    button.setText(it.next());
                } else {
                    button.setText("");
                }
            }

        });


    }


    public void addWordToMessage(ActionEvent e){
        String word = dictionaryCbox.getSelectionModel().getSelectedItem();
        String message = this.originalMessage.getText();
        if(!message.isEmpty()) {
            message = message + " " + word;
        }else{
            message = word;
        }
        this.updateSecretMessage(message);
        this.originalMessage.setText(message);

    }

    private void updateSecretMessage(String original) {
        String encrypted = MachineModel.getInstance().processText(original,false,true);
        this.secretMessage.setText(encrypted);
    }


    public void startBruteForce(ActionEvent event) throws IOException {
        // Loading new view
        String path = "/enigma-fxml/bruteforce/bruteforceProgress.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        Parent view = loader.load();
        this.rootPaneBF.setCenter(view);
        this.rootPaneBF.setLeft(null);
        this.rootPaneBF.setTop(null);
        BruteforceProgressViewController bfViewController = loader.getController();
        bfViewController.initView(collectUserInput(), this);
    }





    private BruteforceUserInput collectUserInput() {
        int difficulty = this.difficultyCbox.getSelectionModel().getSelectedIndex();
        int agents = (int) this.agentCountSlider.getValue();

        if(agents <= 0){
            agents = 1;
        }
        String secret = this.secretMessage.getText();
        int taskSize = 900;
        try {
            String taskSizeText = taskSizeTF.getText();
            taskSize = Integer.parseInt(taskSizeTF.getText());

        }catch(NumberFormatException e){
            createAlert("Wrong input for task size. Default value was set to 900 combination for a task");
        }
        double rotorAlphabet = model.getAlphabetProperty().get().length();
        double numberOfMachineRotors = (double)model.getNumberOfMachineRotors();
        int maxTaskSize = (int) Math.pow(rotorAlphabet, numberOfMachineRotors);
        if(taskSize > maxTaskSize){
            taskSize = maxTaskSize;
        }


        return new BruteforceUserInput(difficulty+1, secret, agents,taskSize);
    }



    private void createAlert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION,message);
        alert.setHeaderText("Wrong Input :(");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/images/turing_icon.png"));
        alert.showAndWait();
        return;
    }

    public void autoComplete(ActionEvent event){
        Button autoComplete = (Button) event.getSource();
        String word = autoComplete.getText();
        String message = this.originalMessage.getText();

        if(!message.isEmpty()) {
            message = message + " " + word;
        }else{
            message = word;
        }
        this.updateSecretMessage(message);
        this.originalMessage.setText(message);
    }




}
