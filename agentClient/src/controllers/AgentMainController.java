package controllers;

import bruteforce.AgentSolutionEntry;
import bruteforce.AgentTask;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import logic.BruteForceAgentTask;
import machine.Engine;
import machine.IEngine;
import users.AgentEntry;
import users.UBoat;

import java.util.Timer;

import static utils.Constants.GSON_INSTANCE;
import static utils.Constants.REFRESH_RATE;

public class AgentMainController {
    private Stage stage;

    private AgentEntry agent;

    @FXML Label agentName;
    @FXML Label teamLabel;
    @FXML Label statusMessageLabel;
    @FXML Label tasksInQueueLabel;
    @FXML Label tasksCompletedLabel;
    @FXML Label workersLabel;

    @FXML
    TableView<AgentSolutionEntry> candidateTable;
    @FXML
    TableColumn<AgentSolutionEntry,String> candidateCol;
    @FXML TableColumn<AgentSolutionEntry, String> agentNameCol;
    @FXML TableColumn<AgentSolutionEntry,String> teamNameCol;
    @FXML TableColumn<AgentSolutionEntry,String> machineCodeCol;

    private IntegerProperty tasksCompleted;
    private IntegerProperty tasksInQueue;
    private AgentEntry agentEntry;

    private IEngine enigmaEngine;

    Timer timer;


    @FXML public void initialize(){
        candidateCol.setCellValueFactory(
                cellData -> cellData.getValue().CandidateProperty()
        );
        agentNameCol.setCellValueFactory(
                cellData -> cellData.getValue().AgentNameProperty()
        );
        teamNameCol.setCellValueFactory(
                cellData -> cellData.getValue().TeamNameProperty()
        );
        machineCodeCol.setCellValueFactory(
                cellData -> cellData.getValue().MachineCodeProperty()
        );
        AgentSolutionEntry ase = new AgentSolutionEntry("omer", "rotem", "secret", "<A<B<C>");
        candidateTable.getItems().add(ase);
        tasksCompleted = new SimpleIntegerProperty(0);
        tasksCompletedLabel.textProperty().bind(tasksCompleted.asString());

        tasksInQueue = new SimpleIntegerProperty(0);
        tasksInQueueLabel.textProperty().bind(tasksInQueue.asString());

        startGetEnigmaTimerTask();


    }



    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setAgentEntry(AgentEntry agent) {
        this.agentEntry = agent;
        workersLabel.setText(String.valueOf(agentEntry.getWorkersCount()));
        agentName.setText("Agent " + agentEntry.username);
        teamLabel.setText("Team " + agentEntry.getTeamName());
        statusMessageLabel.setText("Waiting for contest to start");
    }


    private void startGetEnigmaTimerTask() {
        String teamName = agentEntry.getTeamName();
        RequestEnigmaTimerTask enigmaRequest = new RequestEnigmaTimerTask(teamName, this::startContest);
        timer = new Timer();
        timer.schedule(enigmaRequest, REFRESH_RATE, REFRESH_RATE);
    }

    public void startContest(UBoat uboat){
        if(uboat.getMachine() == null){
            return;
        }
        // Load Enigma
        Engine engine = new Engine();
        engine.loadCTEnigma(uboat.getMachine());
        BruteForceAgentTask bfTask = new BruteForceAgentTask(engine, uboat, this.agentEntry);
    }


}
