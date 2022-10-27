package controllers.bruteforce;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.io.IOException;


public class BruteforceProgressViewController {


    @FXML BorderPane rootPaneBF;
    @FXML ProgressBar taskProgressBar;


    // All Labels
    @FXML Label taskMessageLabel;
    @FXML Label taskSizeLabel;
    @FXML Label agentCountLabel;
    @FXML Label difficultyLabel;

    // Label equals how many solutions found
    @FXML Label solutionCountLabel;


    @FXML Button startButton;
    @FXML Button pauseButton;
    @FXML Button stopButton;

    @FXML Button resumeButton;

    @FXML TableView<BruteforceSolution> solutionTableView;

    @FXML TableColumn<BruteforceSolution, String> agentColumn;
    @FXML TableColumn<BruteforceSolution, String> solutionColumn;

    ObservableList<BruteforceSolution> solutionObservableList;

    SimpleLongProperty tasksCompleted;

    BruteForceController bfController;



    private BruteforceTask bruteforceTask;

    private boolean taskWasPaused;







    public BruteforceProgressViewController(){
        this.solutionObservableList = FXCollections.observableArrayList();

        tasksCompleted = new SimpleLongProperty(0);

        // default value
        taskWasPaused = false;


    }

    @FXML
    private void initialize(){
        startButton.setDisable(false);
        pauseButton.setDisable(true);
        stopButton.setDisable(true);

        // Setting table columns
        agentColumn.setCellValueFactory(
                new PropertyValueFactory<>("agentName")
        );

        solutionColumn.setCellValueFactory(
                new PropertyValueFactory<>("solutionCandidate")
        );

    }


    public void initView(BruteforceUserInput userInput, BruteForceController bfController){
        this.agentCountLabel.setText(String.valueOf(userInput.getAgents()));
        this.difficultyLabel.setText(String.valueOf(userInput.getDifficulty()));
        this.taskSizeLabel.setText(String.valueOf(userInput.getTaskSize()));

        // Creating adapter for class
        UIAdapter adapter = this.createUIAdapter();
        this.bruteforceTask = new BruteforceTask(userInput,adapter);

        // binding progress bar
        taskProgressBar.progressProperty().bind(bruteforceTask.progressProperty());


        // binding with task message
        taskMessageLabel.textProperty().bind(bruteforceTask.messageProperty());


        agentCountLabel.setText(String.valueOf(userInput.getAgents()));

        taskSizeLabel.setText(String.valueOf(userInput.getTaskSize()));

        // setting list of rows for table
        solutionTableView.setItems(this.solutionObservableList);

        this.bfController = bfController;
        pauseButton.setDisable(true);
        resumeButton.setDisable(true);
        stopButton.setDisable(true);

    }


    public void startTask(ActionEvent actionEvent) {
        Thread task = new Thread(this.bruteforceTask);
        task.setDaemon(true);
        task.start();

        startButton.setDisable(true);
        resumeButton.setDisable(true);
        pauseButton.setDisable(false);
        stopButton.setDisable(false);
    }

    public void pauseTask(ActionEvent actionEvent) {
        this.bruteforceTask.pauseTask();
        pauseButton.setDisable(true);
        stopButton.setDisable(false);
        resumeButton.setDisable(false);
    }

    public void stopTask(ActionEvent actionEvent) throws IOException {
        this.bruteforceTask.cancel();

        String path = "/enigma-fxml/bruteforce/bruteforce.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        Parent view = loader.load();
        BruteForceController bfMenuController = (BruteForceController) loader.getController();
        bfMenuController.initialize();
        this.rootPaneBF.setCenter(view);
        this.rootPaneBF.setLeft(null);
        this.rootPaneBF.setTop(null);

    }




    private UIAdapter createUIAdapter(){
        return new UIAdapter(this::addSolutionToTable,this.tasksCompleted::set);
    }

    public void resumeTask(ActionEvent actionEvent) {
        if(bruteforceTask != null){
            this.bruteforceTask.resumeTask();
        }
        pauseButton.setDisable(false);
        stopButton.setDisable(false);
        resumeButton.setDisable(true);
    }

    public void addSolutionToTable(BruteforceSolution solution){
        this.solutionObservableList.add(solution);
        this.solutionCountLabel.setText(String.valueOf(solutionObservableList.size()));
    }


}
