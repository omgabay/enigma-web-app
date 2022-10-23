package controllers;
import auxiliary.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import model.MachineModel;


public class OperatorController{

    @FXML TextField userTextField;

    @FXML TextArea enigmaTextArea;
    @FXML Button processButton;
    @FXML Button clearButton;

    @FXML TableView<Message> statsTable;
    TableColumn<Message,String> originalMsgColumn;
    TableColumn<Message,String> processedMsgColumn;
    TableColumn<Message, Integer> timeColumn;

    @FXML TextField enigmaManualModeTF;
    @FXML TextField enigmaOutputManualModeTF;
    @FXML Text ErrorText;


    @FXML Label machineConfigurationTxt;

    private MachineModel model;


    public OperatorController(){

    }

    @FXML
    public void initialize(){
        System.out.println("printing from operator controller");
        originalMsgColumn = new TableColumn<>("Original Message");
        processedMsgColumn = new TableColumn<>("Processed Message");
        timeColumn = new TableColumn<>("Time(ns)");

        // Creating CellValueFactory to fill the table with data from model
        originalMsgColumn.setCellValueFactory(new PropertyValueFactory<Message,String>("original"));
        processedMsgColumn.setCellValueFactory(  new PropertyValueFactory<Message,String>("processed"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<Message,Integer>("timeProcessed"));

        // Adding columns to the table
        statsTable.getColumns().add(originalMsgColumn);
        statsTable.getColumns().add(processedMsgColumn);
        statsTable.getColumns().add(timeColumn);

        this.model = MachineModel.getInstance();
        statsTable.setItems(model.getMessages());

        this.machineConfigurationTxt.textProperty().bind(model.currentConfigurationStringFormatProperty());


    }





    private void keyPressed(String text) {
        System.out.println(text);
    }

    public void keyPressed(ActionEvent actionEvent) {
        Object node = actionEvent.getSource();
        if (node instanceof  Button){
            Button b = (Button) node;
            keyPressed(b.getText());
        }

    }

    public void processEvent(ActionEvent actionEvent) {
        String userInput = this.userTextField.getText();
        String enigmaOutput = MachineModel.getInstance().processText(userInput,true,false);
        String enigmaText = this.enigmaTextArea.getText() + enigmaOutput;
        this.enigmaTextArea.setText(enigmaText);
    }




    public void clearUserInput(ActionEvent event){
        this.userTextField.clear();
        this.enigmaTextArea.setText("");
        this.enigmaManualModeTF.clear();
        this.enigmaOutputManualModeTF.clear();

    }


    public void resetMachineEvent(ActionEvent actionEvent) {
        if(model == null){
            model = MachineModel.getInstance();
        }
        model.resetMachineEvent();
    }

    @FXML
    void ManualModeInputInsertedTF(KeyEvent event) {
        String userInput = event.getText();
        boolean backSpaceInserted =  event.getCode().toString() == "BACK_SPACE";
        String enigmaOutput = MachineModel.getInstance().processText(userInput,false,false);
        setManualModeText(backSpaceInserted, enigmaOutput, userInput);
    }

    void setManualModeText(boolean backSpaceInserted, String enigmaOutput, String userInput) {
        String currentTextManualModeTF = enigmaOutputManualModeTF.getText();
        int currentTextManualModeTFLLength = currentTextManualModeTF.length();

        if (backSpaceInserted &&  (currentTextManualModeTFLLength > 0)) {
            enigmaOutputManualModeTF.setText(currentTextManualModeTF.substring(0, currentTextManualModeTFLLength - 1));
        }

        else if (enigmaOutput.isEmpty()) {
            ErrorText.setText("There is no letter '" + userInput + "' in the machine");
        }
        else {
            String enigmaText = this.enigmaOutputManualModeTF.getText() + enigmaOutput;
            enigmaOutputManualModeTF.setText(enigmaText);
            ErrorText.setText("");
        }
    }



    public void DoneButtonAction(ActionEvent actionEvent) {
        String input = enigmaManualModeTF.getText();
        for(Character ch : input.toCharArray()){
            String enigmaOutput = MachineModel.getInstance().processText(ch.toString(),true,false);
            String enigmaText = this.enigmaTextArea.getText() + enigmaOutput;
            this.enigmaTextArea.setText(enigmaText);
        }
        enigmaManualModeTF.clear();
        enigmaOutputManualModeTF.clear();
    }

}
