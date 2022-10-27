package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import users.AgentEntry;

public class AgentMainController {
    private Stage stage;

    private AgentEntry agent;

    @FXML Label agentName;

    public AgentMainController(AgentEntry agentData){
        this.agent = agentData;
    }




    @FXML public void initialize(){
        agentName.setText("Agent " + agent.getName());

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
