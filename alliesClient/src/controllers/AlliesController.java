package controllers;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.AgentEntry;
import users.UBoat;
import users.User;
import utils.Constants;
import utils.gui.CreateAlertBox;
import utils.http.HttpClientUtil;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Timer;

import static utils.CommonConstants.REFRESH_RATE;
import static utils.Configuration.GSON_INSTANCE;

public class AlliesController implements Closeable {

    @FXML TableView<AgentEntry> agentsTable;
    @FXML TableColumn<AgentEntry, String> agentNameCol;
    @FXML TableColumn<AgentEntry,String> agentWorkersCol;

    @FXML TableColumn<AgentEntry,String> taskSizeCol;

    @FXML TableView<UBoat> uboatsTable;

    @FXML TableColumn<UBoat,String> battleNameCol;
    @FXML TableColumn<UBoat, String> uboatNameCol;
    @FXML TableColumn<UBoat, String> gameStatusCol;

    @FXML TableColumn<UBoat, String> difficultyCol;

    @FXML Tab contestTab;
    @FXML TabPane alliesTabPane;
    @FXML Label allyHeadingLabel;
    @FXML TextField contestPreviewLabel;

    @FXML TextField agentNameLabel;
    @FXML TextField missionSizeTF;
    @FXML Label battleLabel;
    @FXML Label uboatLabel;
    @FXML Label statusLabel;
    @FXML Label difficultyLabel;
    @FXML Label registeredLabel;


    private Stage stage;
    private AlliesClientRefreshTask clientRefresher;




    String clientName;
    UBoat contestUboat;

    BooleanProperty startBruteforceAttack = new SimpleBooleanProperty(false);



    Timer timer;

    public AlliesController(String allyName) {
        this.clientName = allyName;
    }


    @FXML
    public void initialize(){
        contestTab.setDisable(true);
        setupUBoatTable();
        setupAgentTable();
        allyHeadingLabel.setText("Ally " + clientName);


        uboatsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                contestPreviewLabel.setText(newSelection.getName());
            }
        });
        startDashboardRefresher();
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
                cellData -> cellData.getValue().TeamsRegisteredProperty()
        );

        difficultyCol.setCellValueFactory(
                cellData -> cellData.getValue().DifficultyProperty()
        );
    }




    @FXML public void joinContestClicked(){
        if(contestPreviewLabel.getText().isEmpty()){
            return;
        }

        String uboatName = contestPreviewLabel.getText();
        System.out.println("Ally name " + clientName);



        if(agentsTable.getItems().isEmpty()){
            return;
        }
        String finalUrl = HttpUrl
                .parse(Constants.JOIN_CONTEST_URL)
                .newBuilder()
                .addQueryParameter(Constants.UBOAT_PARAM,uboatName)
                .addQueryParameter(Constants.USERNAME, this.clientName)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error in joining contest");

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    Platform.runLater(()->{
                        contestTab.setDisable(false);
                        alliesTabPane.getSelectionModel().select(1);
                        updateContestTab(uboatName);
                    });
                }
            }
        });


    }




    @FXML public void readyBtnClicked(){
        String finalUrl = HttpUrl
                .parse(Constants.READY_UPDATE_URL)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error sending ready update from Allies Client");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String rawBody = response.body().string();
                    User myTeam = GSON_INSTANCE.fromJson(rawBody, User.class);
                    System.out.println(myTeam);
                }
            }
        });


    }



    @FXML public void addAgentToTeam(ActionEvent event){
        if(agentNameLabel.getText().isEmpty()){
            String message = "Please give a name to your agent";
            CreateAlertBox.createAlert(message,stage);
        }

        String agentName = agentNameLabel.getText();
        String workers = String.valueOf(1);
        String taskSize = String.valueOf(10);
        String finalUrl = HttpUrl
                .parse(Constants.ADD_AGENT_TO_TEAM_PAGE)
                .newBuilder()
                .addQueryParameter(Constants.AGENT_PARAM, agentName)
                .addQueryParameter(Constants.AGENT_WORKER_COUNT, workers)
                .addQueryParameter("taskSize", taskSize)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String json = response.body().string();
                    AgentEntry agent = GSON_INSTANCE.fromJson(json,AgentEntry.class);
                    Platform.runLater(() -> {
                        System.out.print(json);
                        addAgentToTable(agent);
                    });
                }
            }
        });

    }

    private void addAgentToTable(AgentEntry agent) {
        this.agentsTable.getItems().add(agent);
    }

    public void updateUboatsTable(List<UBoat> uBoatList){
        uboatsTable.getItems().clear();
        uboatsTable.getItems().addAll(uBoatList);
    }
    public void updateAgentsTable(List<AgentEntry> agentEntriesList){
        agentsTable.getItems().clear();
        agentsTable.getItems().addAll(agentEntriesList);
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }



    private void startDashboardRefresher(){
        this.clientRefresher = new AlliesClientRefreshTask(this.clientName ,this::updateUboatsTable,this::updateAgentsTable);
        timer = new Timer();
        timer.schedule(clientRefresher, REFRESH_RATE, REFRESH_RATE);
    }



    @Override
    public void close() throws IOException {
        if (clientRefresher != null && timer != null) {
            clientRefresher.cancel();
            timer.cancel();
        }
    }


    public void plus50(ActionEvent event){
        int newVal = Integer.parseInt(missionSizeTF.getText()) + 50;
        missionSizeTF.setText(String.valueOf(newVal));
    }

    public void minus50(ActionEvent event){
        int newVal = Integer.parseInt(missionSizeTF.getText()) - 50;
        missionSizeTF.setText(String.valueOf(newVal));
    }

    private void updateContestTab(String uboatName){
        String finalUrl = HttpUrl
                .parse(Constants.GET_USER_RESOURCE_PAGE)
                 .newBuilder()
                 .addQueryParameter(Constants.USERNAME, uboatName)
                 .build()
                 .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.err.println("Error in update contest tab");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String rawBody = response.body().string();
                    UBoat uboat = GSON_INSTANCE.fromJson(rawBody,UBoat.class);
                    Platform.runLater(()->{

                        String battleName = uboat.BattleNameProperty().getValue();
                        String uboatName = uboat.getName();
                        String difficulty = uboat.getDifficulty();
                        String registered = uboat.TeamsRegisteredProperty().getValue();
                        battleLabel.setText(battleName);
                        uboatLabel.setText(uboatName);
                        difficultyLabel.setText((difficulty));
                        registeredLabel.setText(registered);
                    });
                }
            }
        });

    }




}
