package controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import requests.TeamDataRefresher;
import users.AgentEntry;
import users.UBoat;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.util.Timer;

import static utils.CommonConstants.REFRESH_RATE;

public class AlliesController {

    @FXML TableView<AgentEntry> agentsTable;
    @FXML TableColumn<AgentEntry, String> agentNameCol;
    @FXML TableColumn<AgentEntry,Integer> agentWorkersCol;

    @FXML TableColumn<AgentEntry,Long> taskSizeCol;

    @FXML TableView<UBoat> uboatsTable;

    @FXML TableColumn<UBoat,String> battleNameCol;
    @FXML TableColumn<UBoat, String> uboatNameCol;
    @FXML TableColumn<UBoat, String> gameStatusCol;

    @FXML TableColumn<UBoat, String> difficultyCol;

    @FXML Tab contestTab;
    @FXML TabPane alliesTabPane;
    @FXML Label allyHeadingLabel;
    @FXML TextField contestPreviewLabel;

    SimpleStringProperty allyClientName = new SimpleStringProperty("Ally Screen");



    Timer timer;





    @FXML
    public void initialize(){
        contestTab.setDisable(true);

        setupUBoatTable();
        setupAgentTable();
        allyHeadingLabel.textProperty().bind(allyClientName);

        //startAllyClientRefresher();


        uboatsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                contestPreviewLabel.setText(newSelection.getName());
            }
        });

        uboatsTable.getItems().add(new UBoat("Moshe"));
        agentsTable.getItems().add(new AgentEntry("myAgent","bestTeam",900,3));

        allyClientName = new SimpleStringProperty("Ally Team");







    }

    private void setupAgentTable() {
        // Setting table columns for Agents Table
        agentNameCol.setCellValueFactory(
                cellData -> cellData.getValue().NameProperty()
        );

        agentWorkersCol.setCellValueFactory(
                cellData -> cellData.getValue().WorkerCountProperty()
        );

        taskSizeCol.setCellValueFactory(
                cellData -> cellData.getValue().TaskSizeProperty()
        );

    }

    private void setupUBoatTable() {
        // Setting table columns for UBoat

        battleNameCol.setCellValueFactory(
                cellData -> cellData.getValue().BattleNameProperty()
        );

        uboatNameCol.setCellValueFactory(
                cellData -> cellData.getValue().NameProperty()
        );

        gameStatusCol.setCellValueFactory(
                cellData -> cellData.getValue().TeamsRegisterdProperty()
        );

        difficultyCol.setCellValueFactory(
                cellData -> cellData.getValue().DifficultyProperty()
        );
    }

    private void startAllyClientRefresher() {
        TeamDataRefresher teamViewRefresher = new TeamDataRefresher(ally ->{
            agentsTable.getItems().clear();
            agentsTable.setItems(FXCollections.observableArrayList(ally.getAgentList()));
        });

        timer = new Timer();
        timer.schedule(teamViewRefresher,REFRESH_RATE*2,REFRESH_RATE);
    }


    @FXML public void joinContestClicked(){
        if(uboatsTable.getSelectionModel().getSelectedItem() == null){
            return;
        }

        String uboatName = contestPreviewLabel.getText();



        if(agentsTable.getItems().isEmpty()){
            return;
        }
        String finalUrl = HttpUrl
                .parse(Constants.JOIN_CONTEST_URL)
                .newBuilder()
                .addQueryParameter(Constants.UBOAT,uboatName)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    Platform.runLater(()->{
                        contestTab.setDisable(false);
                        alliesTabPane.getSelectionModel().select(1);
                    });
                }
            }
        });


    }

    @FXML public void readyBtnClicked(){

    }










}
