package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import requests.TeamViewRefresher;
import users.AgentEntry;
import users.Difficulty;
import users.GameStatus;
import users.UBoat;
import utils.requests.TeamListRefresher;

import java.util.Timer;

import static utils.Constants.REFRESH_RATE;

public class AlliesController {

    @FXML TableView<AgentEntry> agentsTable;
    @FXML TableColumn<UBoat, String> agentNameCol;
    @FXML TableColumn<UBoat,Integer> agentWorkersCol;

    @FXML TableColumn<UBoat,Integer> taskSizeCol;

    @FXML TableView<UBoat> uboatsTable;

    @FXML TableColumn<UBoat,String> battleNameCol;
    @FXML TableColumn<UBoat, String> uboatNameCol;
    @FXML TableColumn<UBoat, GameStatus> gameStatusCol;

    @FXML TableColumn<UBoat, Difficulty> difficultyCol;

    Timer timer;





    @FXML
    public void initialize(){

        // Setting table columns for Agents Table
        agentNameCol.setCellValueFactory(
                new PropertyValueFactory<>("username")
        );

        agentWorkersCol.setCellValueFactory(
                new PropertyValueFactory<>("workersCount")
        );

        taskSizeCol.setCellValueFactory(
                new PropertyValueFactory<>("taskSize")
        );

        // Setting table columns for UBoat

        battleNameCol.setCellValueFactory(
                new PropertyValueFactory<>("battleName")
        );

        uboatNameCol.setCellValueFactory(
                new PropertyValueFactory<>("username")
        );

        gameStatusCol.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );

        difficultyCol.setCellValueFactory(
                new PropertyValueFactory<>("difficulty")
        );


        TeamViewRefresher teamViewRefresher = new TeamViewRefresher(ally ->{
            agentsTable.getItems().clear();
            agentsTable.setItems(FXCollections.observableArrayList(ally.getAgentList()));
        });

        timer = new Timer();
        timer.schedule(teamViewRefresher,REFRESH_RATE*2,REFRESH_RATE);





    }







}
